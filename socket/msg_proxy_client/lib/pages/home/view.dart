import 'package:flutter/material.dart';
import 'package:msg_proxy_client/models/message.dart';
import 'package:msg_proxy_client/pages/home/list_item.dart';
import 'package:msg_proxy_client/pages/home/logic.dart';

class MyHomePage extends StatefulWidget {
  MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  final List<Message> messages = getMessages(20);

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final TextEditingController msgEditController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      backgroundColor: const Color(0xffeeeeee),
      body: Column(
        children: [
          Expanded(
            child: ListView(
              padding: const EdgeInsets.all(10),
              children: [
                ...widget.messages.map(
                    (e) => ListItem(content: e.content, isOwner: e.isOwner))
              ],
            ),
          ),
          Container(
            height: 80,
            margin: const EdgeInsets.only(left: 16, right: 16),
            child: Row(
              children: [
                Expanded(
                  flex: 5,
                  child: TextField(
                    controller: msgEditController,
                    decoration: const InputDecoration(
                      border: OutlineInputBorder(),
                      labelText: "请输入...",
                      filled: true,
                      fillColor: Colors.white,
                    ),
                    onSubmitted: (text) {
                      debugPrint(text);
                      msgEditController.clear();
                    },
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Container(
                    height: 50,
                    margin: const EdgeInsets.only(left: 10),
                    child: ElevatedButton(
                      onPressed: () {
                        debugPrint(msgEditController.text);
                        msgEditController.clear();
                      },
                      child: const Icon(Icons.send),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
