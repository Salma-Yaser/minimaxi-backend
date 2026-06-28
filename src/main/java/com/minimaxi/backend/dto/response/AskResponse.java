package com.minimaxi.backend.dto.response;

public class AskResponse {
    private boolean success;
    private Data data;

    public AskResponse(String reply, Long conversationId) {
        this.success = true;
        this.data = new Data(reply, conversationId);
    }

    public boolean isSuccess() { return success; }
    public Data getData() { return data; }

    public static class Data {
        private String reply;
        private Long conversationId;

        public Data(String reply, Long conversationId) {
            this.reply = reply;
            this.conversationId = conversationId;
        }

        public String getReply() { return reply; }
        public Long getConversationId() { return conversationId; }
    }
}