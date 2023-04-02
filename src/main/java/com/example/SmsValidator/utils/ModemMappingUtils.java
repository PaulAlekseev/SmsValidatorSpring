package com.example.SmsValidator.utils;

import com.example.SmsValidator.dto.modem.ModemBaseDto;
import com.example.SmsValidator.dto.modem.ModemForProvider;
import com.example.SmsValidator.dto.modem.ModemForUserDto;
import com.example.SmsValidator.entity.ModemEntity;

public class ModemMappingUtils {

    public static ModemBaseDto mapToBaseModemDto(ModemEntity modemEntity) {
        ModemBaseDto modemDto = new ModemBaseDto();
        modemDto.setId(modemEntity.getId());
        modemDto.setIMSI(modemEntity.getIMSI());
        modemDto.setICCID(modemEntity.getICCID());
        modemDto.setBusy(modemEntity.getBusy());
        modemDto.setReservedUntil(modemEntity.getReservedUntil());
        modemDto.setServices(modemEntity.getServices());
        modemDto.setPhoneNumber(modemEntity.getPhoneNumber());
        return modemDto;
    }

    public static ModemForProvider mapToModemForProvider(ModemEntity modemEntity, String port) {
        ModemForProvider modemDto = new ModemForProvider();
        modemDto.setId(modemEntity.getId());
        modemDto.setIMSI(modemEntity.getIMSI());
        modemDto.setICCID(modemEntity.getICCID());
        modemDto.setBusy(modemEntity.getBusy());
        modemDto.setReservedUntil(modemEntity.getReservedUntil());
        modemDto.setServices(modemEntity.getServices());
        modemDto.setPhoneNumber(modemEntity.getPhoneNumber());
        modemDto.setPort(port);
        return modemDto;
    }

    public static ModemEntity mapToModemEntity(ModemBaseDto modemDto) {
        ModemEntity modemEntity = new ModemEntity();
        modemEntity.setId(modemDto.getId());
        modemEntity.setIMSI(modemDto.getIMSI());
        modemEntity.setICCID(modemDto.getICCID());
        modemEntity.setBusy(modemDto.getBusy());
        modemEntity.setReservedUntil(modemDto.getReservedUntil());
        modemEntity.setServices(modemDto.getServices());
        modemEntity.setPhoneNumber(modemDto.getPhoneNumber());
        return modemEntity;
    }

    public static ModemForUserDto mapToModemForUser(ModemEntity modemEntity) {
        ModemForUserDto modemForUser = new ModemForUserDto();
        modemForUser.setId(modemEntity.getId());
        modemForUser.setPhoneNumber(modemEntity.getPhoneNumber());
        return modemForUser;
    }
}
