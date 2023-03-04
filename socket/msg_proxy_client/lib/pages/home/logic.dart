import 'package:msg_proxy_client/models/message.dart';

List<Message> getMessages(int num) {
  List<Message> l = [];
  List<int> ownerIndex = [2, 5, 6, 9, 10, 15, 16, 18];
  for (int i = 0; i < num; i++) {
    bool isOwner = ownerIndex.contains(i);
    String port = isOwner ? "32256" : "32257";
    Message msg = Message(
        address: "127.0.0.1",
        port: port,
        host: "127.0.0.1",
        isOwner: isOwner,
        content: "content_$i");
    l.add(msg);
  }
  return l;
}
