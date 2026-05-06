package com.minimaxi.backend.dto.response;

public class AskResponse {
    private boolean success;
    private Data data;

    public AskResponse(String reply) {
        this.success = true;
        this.data = new Data(reply);
    }

    public boolean isSuccess() { return success; }
    public Data getData() { return data; }

    public static class Data {
        private String reply;
        public Data(String reply) { this.reply = reply; }
        public String getReply() { return reply; }
    }
}