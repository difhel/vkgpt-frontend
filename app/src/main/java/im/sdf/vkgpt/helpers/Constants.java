package im.sdf.vkgpt.helpers;

public final class Constants {
    public static final String CLIENT_SECRET = "qVxWRF1CwHERuIrKBnqe";
    public static final int CLIENT_ID = 6146827;
    public static final String BASE_URL_VK_ME_API = "https://api.vk.com/";
    public static final String BASE_URL_GPT_API = "https://vkgpt.superdef.online/";

    public static final String VKSCRIPT_GET_CONVERSATIONS = "var conversations = API.messages.getConversations({\"count\": 10, \"offset\": 0});\n" +
            "var i = 0;\n" +
            "var contacts = [];\n" +
            "while (i < 10) {\n" +
            "    var t = conversations[\"items\"][i];\n" +
            "    var name = \"UNSUPPORTED\";\n" +
            "    var photo50 = \"\";\n" +
            "    if (t.conversation.peer.type == \"chat\") {\n" +
            "        name = t.conversation.chat_settings.title;\n" +
            "        photo50 = t.conversation.chat_settings.photo.photo_50;\n" +
            "    }\n" +
            "    else {\n" +
            "        if (t.conversation.peer.type == \"user\") {\n" +
            "            var userAbout = API.users.get({\"user_ids\": t.conversation.peer, \"fields\": \"photo_50\"})[0];\n" +
            "            name = userAbout.first_name + \" \" + userAbout.last_name;\n" +
            "            photo50 = userAbout.photo_50;\n" +
            "        }\n" +
            "        else {\n" +
            "            if (t.conversation.peer.type == \"group\") {\n" +
            "                var groupAbout = API.groups.getById({\"group_ids\": t.conversation.peer.local_id})[0];\n" +
            "                photo50 = groupAbout.photo_50;\n" +
            "                name = groupAbout.name;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    var unread_count = 0;\n" +
            "    if (t.conversation.unread_count) {\n" +
            "        unread_count = t.conversation.unread_count;\n" +
            "    }\n" +
            "    var last_message_from = \"\";\n" +
            "    if (t.last_message) {\n" +
            "        if (t.last_message.peer_id < 0) {\n" +
            "            var groupAbout = API.groups.getById({\"group_ids\": -1 * t.last_message.from_id})[0];\n" +
            "                last_message_from = groupAbout.name;\n" +
            "        }\n" +
            "        else {\n" +
            "            var userAbout = API.users.get({\"user_ids\": t.last_message.from_id})[0];\n" +
            "            last_message_from = userAbout.first_name + \" \" + userAbout.last_name;\n" +
            "        }\n" +
            "    }\n" +
            "    contacts.push({\n" +
            "\t\t      \"peer_id\": t.conversation.peer.id,\n" +
            "              \"name\": name,\n" +
            "              \"photo50\": photo50,\n" +
            "              \"unread_count\": unread_count,\n" +
            "              \"last_message\": {\n" +
            "                  \"from_name\": last_message_from,\n" +
            "                  \"id\": t.last_message.id,\n" +
            "                  \"from_id\": t.last_message.from_id,\n" +
            "                  \"date\": t.last_message.date,\n" +
            "                  \"text\": t.last_message.text,\n" +
            "                  \"attachments\": t.last_message.attachments,\n" +
            "                  \"reply_message\": t.last_message.reply_message\n" +
            "              }\n" +
            "\t\t\t\n" +
            "\t\t  });\n" +
            "    i = i + 1;\n" +
            "}\n" +
            "return contacts;";
    public static final String VKSCRIPT_GET_MESSAGES = "var request = API.messages.getHistory({\n" +
            "  peer_id: Args.peer_id,\n" +
            "  count: 10,\n" +
            "  offset: Args.offset,\n" +
            "  v: \"5.131\"\n" +
            "});\n" +
            "var i = 0;\n" +
            "var messages = [];\n" +
            "while (i < 10) {\n" +
            "  var m = request['items'][i];\n" +
            "  var last_message_from = 'UNSUPPORTED';\n" +
            "  var photo50 = '';\n" +
            "  if (m.from_id < 0) {\n" +
            "    var groupAbout = API.groups.getById({ group_ids: -1 * m.from_id })[0];\n" +
            "    last_message_from = groupAbout.name;\n" +
            "    photo50 = groupAbout.photo_50;\n" +
            "  } \n" +
            "  else {\n" +
            "    var userAbout = API.users.get({ user_ids: m.from_id, fields: \"photo_50\" })[0];\n" +
            "    last_message_from = userAbout.first_name + ' ' + userAbout.last_name;\n" +
            "    photo50 = userAbout.photo_50;\n" +
            "  }\n" +
            "  messages.push({\n" +
            "    \"from_name\": last_message_from,\n" +
            "    \"photo50\": photo50,\n" +
            "    \"id\": m.id,\n" +
            "    \"from_id\": m.from_id,\n" +
            "    \"date\": m.date,\n" +
            "    \"text\": m.text,\n" +
            "    \"attachments\": m.attachments,\n" +
            "    \"reply_message\": m.reply_message,\n" +
            "  });\n" +
            "  i = i + 1;\n" +
            "}\n" +
            "return messages;\n";
    public static final String VKSCRIPT_GET_CONVERSATION = "var t = API.messages.getConversationsById({ \"peer_ids\": Args.peer_id }).items[0];\n" +
            "var name = \"UNSUPPORTED\";\n" +
            "var photo50 = \"\";\n" +
            "if (t.peer.type == \"chat\") {\n" +
            "    name = t.chat_settings.title;\n" +
            "    photo50 = t.chat_settings.photo.photo_50;\n" +
            "}\n" +
            "else {\n" +
            "    if (t.peer.type == \"user\") {\n" +
            "        var userAbout = API.users.get({ \"user_ids\": t.peer, \"fields\": \"photo_50\" })[0];\n" +
            "        name = userAbout.first_name + \" \" + userAbout.last_name;\n" +
            "        photo50 = userAbout.photo_50;\n" +
            "    }\n" +
            "    else {\n" +
            "        if (t.peer.type == \"group\") {\n" +
            "            var groupAbout = API.groups.getById({ \"group_ids\": t.peer.local_id })[0];\n" +
            "            photo50 = groupAbout.photo_50;\n" +
            "            name = groupAbout.name;\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "var unread_count = 0;\n" +
            "if (t.unread_count) {\n" +
            "    unread_count = t.unread_count;\n" +
            "}\n" +
            "var last_message_from = \"\";\n" +
            "var last_message = API.messages.getHistory({\"peer_id\": Args.peer_id, \"count\": 1}).items[0];\n" +
            "if (last_message.peer_id < 0) {\n" +
            "    var groupAbout = API.groups.getById({ \"group_ids\": -1 * last_message.from_id })[0];\n" +
            "    last_message_from = groupAbout.name;\n" +
            "}\n" +
            "else {\n" +
            "    var userAbout = API.users.get({ \"user_ids\": last_message.from_id })[0];\n" +
            "    last_message_from = userAbout.first_name + \" \" + userAbout.last_name;\n" +
            "}\n" +
            "return {\n" +
            "    \"peer_id\": t.peer.id,\n" +
            "    \"name\": name,\n" +
            "    \"photo50\": photo50,\n" +
            "    \"unread_count\": unread_count,\n" +
            "    \"last_message\": {\n" +
            "        \"from_name\": last_message_from,\n" +
            "        \"id\": last_message.id,\n" +
            "        \"from_id\": last_message.from_id,\n" +
            "        \"date\": last_message.date,\n" +
            "        \"text\": last_message.text,\n" +
            "        \"attachments\": last_message.attachments,\n" +
            "        \"reply_message\": last_message.reply_message\n" +
            "    }\n" +
            "};";
}
