package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WhatsAppMessageDTO {
    @JsonProperty("messaging_product")
    private String messagingProduct = "whatsapp";

    private String to;
    private String type = "text";
    private TextContent text;

    public WhatsAppMessageDTO(String to, String message) {
        this.to = to;
        this.text = new TextContent(message);
    }

    public String getMessagingProduct() {
        return messagingProduct;
    }

    public void setMessagingProduct(String messagingProduct) {
        this.messagingProduct = messagingProduct;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TextContent getText() {
        return text;
    }

    public void setText(TextContent text) {
        this.text = text;
    }

    public static class TextContent {
        private String body;

        public TextContent(String body) {
            this.body = body;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}