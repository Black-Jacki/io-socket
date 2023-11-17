import 'package:flutter/material.dart';
import 'package:msg_proxy_client/constants.dart';
import 'package:msg_proxy_client/pages/home/view.dart';
import 'package:msg_proxy_client/pages/login/view.dart';
import 'package:msg_proxy_client/pages/message/view.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: Constants.appTitle,
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      routes: {
        "/": (context) => const HomePage(),
        "/login": (context) => const LoginPage(),
        "/message": (context) => const MessagePage(),
      },
    );
  }
}
