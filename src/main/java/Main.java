
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        System.out.println("Using Telegram Bot without Library!");
        System.out.println(" ");
        
        String token = System.getenv("TELEGRAM_TOKEN");

        if ((token==null) || (token.length()==0)) {
            System.out.println("Error: TOKEN env not found. Define in linux with -> export TELEGRAM_TOKEN={your telegram bot token}");
            System.exit(0);
        }

        String botToken = "bot" + token;
        String urlApiTelegram = "https://api.telegram.org/";
        String urlApiTelegramBot = new StringBuilder(urlApiTelegram).append("{botToken}").toString();

        try {
            String urlApiGetMe = new StringBuilder(urlApiTelegramBot).append("/getMe").toString();

            String id = Unirest.get(urlApiGetMe).routeParam("botToken", botToken)
                    .asJson()
                    .getBody()
                    .getObject()
                    .getJSONObject("result")
                    .getString("id");

            System.out.println("Telegram API : /getMe");
            System.out.println("My ID : " + id);

        } catch (JSONException jsonException) {
            System.out.println("jsonException /getMe: " + jsonException.getMessage());
        }

        String urlApiGetUpdates = new StringBuilder(urlApiTelegramBot).append("/getUpdates").toString();

        try {
            System.out.println(" ");
            System.out.println("Telegram API : /getUpdates");
            JSONObject jsonObject = Unirest.get(urlApiGetUpdates).routeParam("botToken", botToken)
                    .asJson()
                    .getBody()
                    .getObject()
                    .getJSONArray("result")
                    .getJSONObject(0);

            JSONObject jsonObjectMessage = jsonObject.getJSONObject("message");

            String urlApiSendMessage = new StringBuilder(urlApiTelegramBot).append("/sendMessage").toString();

            for (int i = 0; i < jsonObject.length(); i++) {

                String chatId = jsonObjectMessage.getJSONObject("chat").getString("id");                
                String firstName = jsonObjectMessage.getJSONObject("chat").getString("first_name");
                String userName = jsonObjectMessage.getJSONObject("chat").getString("username");
                String messageTextWelcome = "Hi " + firstName + "! \nWelcome to the new Bot " + userName;

                HttpResponse<JsonNode> response = Unirest.post(urlApiSendMessage)
                        .routeParam("botToken", botToken)
                        .queryString("chat_id", chatId)
                        .queryString("text", messageTextWelcome)
                        .asJson();

                System.out.println("SendMessage to: " + userName);
                System.out.println("Status: " + response.getStatusText());
                System.out.println("Response Body:");
                System.out.println(response.getBody().toString());
                System.out.println("--------------");
                
            }

        } catch (JSONException jsonException) {
            System.out.println("jsonException /getUpdates: " + jsonException.getMessage());
        }

    }
}