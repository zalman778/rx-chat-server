package com.hwx.rx_chat_server.controller;

import com.hwx.rx_chat.common.entity.st.Dialog;
import com.hwx.rx_chat.common.entity.st.UserEntity;
import com.hwx.rx_chat.common.response.DefaultResponse;
import com.hwx.rx_chat.common.response.DialogProfileResponse;
import com.hwx.rx_chat.common.response.DialogResponse;
import com.hwx.rx_chat.common.response.FriendResponse;
import com.hwx.rx_chat_server.repository.custom.DialogCustomRepository;
import com.hwx.rx_chat_server.repository.st.DialogStaticRepository;
import com.hwx.rx_chat_server.service.st.DialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DialogController {

    @Autowired
    private DialogService dialogService;

    @Autowired
    private DialogCustomRepository dialogCustomRepository;

    @Autowired
    private DialogStaticRepository dialogStaticRepository;

    @RequestMapping(value = "/api/dialogs", method = RequestMethod.POST, produces = "application/json")
    public List<DialogResponse> getDialogList(@RequestParam String userId) {
        return dialogCustomRepository.findLastDialogs(userId);
    }

    @GetMapping(value="/api/dialog/info/{dialogId}")
    public DialogProfileResponse getDialogInfo(@PathVariable String dialogId) {
        Dialog dialog =  dialogStaticRepository.findById(dialogId).get();
        DialogProfileResponse dialogResponse = new DialogProfileResponse();
        dialogResponse.setDialogId(dialog.getId());
        dialogResponse.setChatImage(dialog.getImageUrl());
        dialogResponse.setDialogName(dialog.getName());
        if (dialog.getUserCreated() != null)
            dialogResponse.setCreatorId(dialog.getUserCreated().getId());
        for (UserEntity user : dialog.getMembers()) {
            FriendResponse friendResponse = new FriendResponse();
            friendResponse.setUserId(user.getId());
            friendResponse.setImageUrl(user.getAvatarUrl());
            friendResponse.setUsername(user.getUsername());
            dialogResponse.getFriendList().add(friendResponse);
        }
        return dialogResponse;
    }

    //получение id диалога (поиск существующего или новый) по айди 2 юзеров:
    @GetMapping(value="/api/dialog/find_or_create/{userA}/{userB}")
    public DefaultResponse findOrCreateDialog(
              @PathVariable String userA
            , @PathVariable String userB
    ) {
        try {
            String dialogID = dialogService.findOrCreateDialogIdByUserAAndUserB(userA, userB);
            return new DefaultResponse("ok", "ok", dialogID);
        } catch (Exception e) {
            return new DefaultResponse("err", "err on processing request");
        }

    }

    @RequestMapping(value = "/api/dialog/create", method = RequestMethod.POST, produces = "application/json")
    public DefaultResponse createDialog(
            @RequestParam List<String> pickedProfiles
            , @RequestParam String dialogCaption
    ) {
        try {
            String dialogID = dialogService.createDialog(pickedProfiles, dialogCaption);
            return new DefaultResponse("ok", "ok", dialogID);
        } catch (Exception e) {
            return new DefaultResponse("err", "err on processing request");
        }
    }

    //удаление пользователя из диалога:
    @GetMapping(value="/api/dialog/delete_member/{dialogId}/{userId}")
    public DefaultResponse deleteDialogMember(
              @PathVariable String dialogId
            , @PathVariable String userId
    ) {
        try {
            dialogService.deleteDialogMember(dialogId, userId);
            return new DefaultResponse("ok", "ok");
        } catch (Exception e) {
            return new DefaultResponse("err", "err on processing request");
        }

    }
}
