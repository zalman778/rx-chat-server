package com.hwx.rx_chat_server.service.st;

import java.util.List;

public interface DialogService {
    String findOrCreateDialogIdByUserAAndUserB(String userIdA, String userIdB);

    String createDialog(List<String> pickedProfiles, String dialogCaption);

    void deleteDialogMember(String dialogId, String userId);
}
