package com.example.SmsValidator.service;

import com.example.SmsValidator.auth.AuthEncryptor;
import com.example.SmsValidator.bean.authentication.RestorePasswordData;
import com.example.SmsValidator.bean.authentication.ValidationData;
import com.example.SmsValidator.bean.authentication.providerfactory.RestoreInfoProviderFactory;
import com.example.SmsValidator.bean.authentication.providerfactory.VerifyInfoProviderFactory;
import com.example.SmsValidator.bean.authentication.request.RegisterRequest;
import com.example.SmsValidator.bean.authentication.request.RestorePasswordChangeRequest;
import com.example.SmsValidator.bean.authentication.request.RestorePasswordRequest;
import com.example.SmsValidator.bean.authentication.response.RegistrationSuccessResponse;
import com.example.SmsValidator.bean.authentication.response.RestorePasswordSuccessResponse;
import com.example.SmsValidator.bean.authentication.response.VerifySuccessResponse;
import com.example.SmsValidator.entity.Role;
import com.example.SmsValidator.entity.User;
import com.example.SmsValidator.exception.customexceptions.user.*;
import com.example.SmsValidator.repository.UserRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    public static final int ALLOWED_PASSWORD_LENGTH = 6;
    private final UserRepository repository;
    private final AuthEncryptor authEncryptor;
    private final MailService mailService;
    private final VerifyInfoProviderFactory verifyInfoProvider;
    private final RestoreInfoProviderFactory restoreInfoProvider;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    public boolean requestIsOutdated(Date created, int amount, int compareOnType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(compareOnType, amount);
        return created.before(calendar.getTime());
    }

    public String createRestoreKey(String email, Long userId, Date lastUpdated, Date requestCreated)
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        RestorePasswordData restorePasswordData = RestorePasswordData
                .builder()
                .email(email)
                .userId(userId)
                .lastUpdated(lastUpdated)
                .requestCreated(requestCreated)
                .build();
        return authEncryptor.encrypt(new Gson().toJson(restorePasswordData), restoreInfoProvider);
    }

    public void sendEmailForRestorePassword(String email, Long userId, Date lastUpdated, Date requestCreated)
            throws Exception {
        URI uri = URI.create(restoreInfoProvider.getUrl() + createRestoreKey(email, userId, lastUpdated, requestCreated));
        String emailText = String.format("""
                Hello!
                You are receiving this email because we received a restore password request for this mail account.
                Restore password link: %s
                """, uri);
        mailService.sendEmail(email, "Restore password", emailText);
    }

    private User validateUser(User user, ValidationData validationData) throws CouldNotValidateUser {
        if (!(Objects.equals(user.getEmail(), validationData.getEmail()) &&
                Objects.equals(user.getId(), validationData.getId()) &&
                (validationData.getCreated().compareTo(user.getCreated()) == 0) &&
                !user.getEnabled())) {
            throw new CouldNotValidateUser("Could not validate user " + validationData.getEmail(), this.getClass());
        }
        user.setEnabled(true);
        return repository.save(user);
    }

    private void validateRestorePasswordData(RestorePasswordData restorePasswordData, User user) throws RequestNotValidException, RequestIsOutdatedException {
        if (!Objects.equals(restorePasswordData.getEmail(), user.getEmail()))
            throw new RequestNotValidException("Request is not valid", this.getClass());
        if (requestIsOutdated(restorePasswordData.getRequestCreated(), -1, Calendar.HOUR) ||
                !Objects.equals(restorePasswordData.getLastUpdated(), user.getUpdated()))
            throw new RequestIsOutdatedException("Request is outdated", this.getClass());
    }

    private User findUser(String email) throws UserNotFoundException {
        return repository.findByEmailAndEnabledTrue(email)
                .orElseThrow(() -> new UserNotFoundException("User not found", this.getClass()));
    }

    private User getExistingUser(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public RestorePasswordSuccessResponse startRestorePassword(RestorePasswordRequest request)
            throws Exception {
        logger.info("Starting restore password on " + request.getEmail());
        User user = findUser(request.getEmail());

        sendEmailForRestorePassword(request.getEmail(), user.getId(), user.getUpdated(), new Date());
        logger.info("Successfully sent restore password email on " + request.getEmail());
        return new RestorePasswordSuccessResponse();
    }

    public RestorePasswordSuccessResponse restorePassword(RestorePasswordChangeRequest request, String key)
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, UserNotFoundException, RequestNotValidException, RequestIsOutdatedException {
        logger.info("Starting restore password process " + key);
        RestorePasswordData restorePasswordData = (RestorePasswordData) authEncryptor.decrypt(key, restoreInfoProvider);
        User user = findUser(restorePasswordData.getEmail());
        validateRestorePasswordData(restorePasswordData, user);
        repository.updatePasswordAndUpdatedById(
                passwordEncoder.encode(request.getNewPassword()),
                new Date(),
                user.getId()
        );
        logger.info("Successfully restored password for " + restorePasswordData.getEmail());
        return new RestorePasswordSuccessResponse();
    }

    public VerifySuccessResponse verify(String encryptedUserData)
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, RequestIsOutdatedException, UserNotFoundException, CouldNotValidateUser {
        logger.info("Starting verify process " + encryptedUserData);
        ValidationData validationData = (ValidationData) authEncryptor.decrypt(encryptedUserData, verifyInfoProvider);
        User user = getExistingUser(validationData.getEmail());
        if (user == null) throw new UserNotFoundException("User not found", this.getClass());
        if (requestIsOutdated(user.getCreated(), -1, Calendar.DAY_OF_MONTH))
            throw new RequestIsOutdatedException("Request is outdated", this.getClass());
        User newUser = validateUser(user, validationData);
        logger.info("Successfully validated user " + newUser.getEmail());
        return new VerifySuccessResponse();
    }

    private User createNewUser(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(false)
                .created(new Date())
                .updated(new Date())
                .build();

        return repository.save(user);
    }

    public String createValidationKey(String email, Long id, Date created)
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        ValidationData validationData = ValidationData
                .builder()
                .email(email)
                .id(id)
                .created(created)
                .build();
        return authEncryptor.encrypt(new Gson().toJson(validationData), verifyInfoProvider);
    }

    public void sendEmailForValidation(String email, Long userId, Date userCreated)
            throws Exception {
        URI uri = URI.create(verifyInfoProvider.getUrl() + createValidationKey(email, userId, userCreated));
        String emailText = String.format("""
                Hello!
                You are receiving this email because we received a registration request for this mail account. E-mail verification is required to sign in.
                Verification link: %s
                """, uri);
        mailService.sendEmail(email, "Validation", emailText);
    }

    private void deleteUser(User oldUser) {
        repository.deleteById(oldUser.getId());
        logger.info("Deleted user " + oldUser.getEmail());
    }

    private void insureUserIsNotActive(User user) throws UserAlreadyExistException {
        if (user.isEnabled()) {
            throw new UserAlreadyExistException("User with this email already exist", this.getClass());
        }
    }

    public void insureUserDoesNotExist(String email) throws UserAlreadyExistException {
        User oldUser = getExistingUser(email);
        if (oldUser != null) {
            insureUserIsNotActive(oldUser);
            if (requestIsOutdated(oldUser.getCreated(), -1, Calendar.DAY_OF_MONTH)) {
                deleteUser(oldUser);
            }
        }
    }

    public void validateUserPassword(String password) throws NotAllowedPasswordException {
        if (password.length() < ALLOWED_PASSWORD_LENGTH) {
            throw new NotAllowedPasswordException("We cant accept password with length less than " + ALLOWED_PASSWORD_LENGTH,
                    this.getClass());
        }
    }

    public RegistrationSuccessResponse register(RegisterRequest request)
            throws Exception {
        logger.info("Starting registration of " + request.getEmail());
        validateUserPassword(request.getPassword());
        insureUserDoesNotExist(request.getEmail());
        User user = createNewUser(request);
        sendEmailForValidation(user.getEmail(), user.getId(), user.getCreated());
        logger.info("Successfully sent registration email to " + request.getEmail());
        return new RegistrationSuccessResponse("We successfully sent email");
    }
}
