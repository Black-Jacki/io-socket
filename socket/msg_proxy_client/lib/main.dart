import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);
  
  final String title = "Msg Proxy Client";

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: title,
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: title),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Container(
        padding: const EdgeInsets.all(10),
        child: Column(
          children: [
            Expanded(child: Column(
              children: [
                Card(child: Text("消息列表"))
              ],
            )),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    decoration: InputDecoration(
                      border: OutlineInputBorder(),
                      labelText: "请输入...",
                    ),
                    onSubmitted: (text) {
                      debugPrint(text);
                    },
                  ),
                  flex: 6
                ),
                Expanded(
                  child: ElevatedButton(
                      onPressed: () {},
                      child: const Icon(Icons.send)
                  ),
                  flex: 1
                )
              ],
            )
          ],
        ),
      )
    );
  }
}
