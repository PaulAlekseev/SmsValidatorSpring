package com.example.SmsValidator.socket.handler;

import com.example.SmsValidator.socket.command.*;

public class DecideTaskHandler {

    public static Command decideTask(String command) {
        return switch (command) {
            case "SaveModems" -> new ModemsSaveCommand();
            case "ProviderBusy" -> new ProviderNotReadyCommand();
            case "ProviderReady" -> new ProviderReadyCommand();
            case "ModemSetBusy" -> new ModemSetBusyCommand();
            case "ModemSetReady" -> new ModemSetNotBusyCommand();
            case "ModemBusy" -> new ModemBusyCommand();
            case "ModemCheck" -> new ModemCheckCommand();
            case "HandleBlankModems" -> new HandleBlankModemsCommand();
            case "ConnectModem" -> new ConnectModemCommand();
            case "Messages" -> new MessagesCommand();
            case "TaskSetDone" -> new TaskDoneCommand();
            case "DisconnectModems" -> new DisconnectModemsCommand();
            case "AddModems" -> new AddModemsCommand();
            default -> null;
        };
    }
}
