package com.hwx.rx_chat_server.service.st;

public interface DialogService {
    String findOrCreateDialogIdByUserAAndUserB(String userIdA, String userIdB);
}
