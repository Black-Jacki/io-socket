import 'package:flutter/material.dart';
import 'package:msg_proxy_client/constants.dart';
import 'package:shared_preferences/shared_preferences.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late SharedPreferences prefs;
  String hostName = "";

  void init() async {
    prefs = await SharedPreferences.getInstance();
    checkLogin();
  }

  void checkLogin() async {
    bool isLogin = prefs.getBool("isLogin") ?? false;
    if (!isLogin) {
      prefs.remove("hostName");
      Navigator.pushNamedAndRemoveUntil(context, "/login", (route) => false);
    } else {
      setState(() {
        hostName = prefs.getString("hostName") ?? "";
      });
    }
  }

  @override
  void initState() {
    super.initState();
    init();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(Constants.appTitle),
        actions: [
          IconButton(
            onPressed: () {
              prefs.remove("isLogin");
              prefs.remove("hostName");
              Navigator.pushNamedAndRemoveUntil(
                context,
                "/login",
                (route) => false,
              );
            },
            icon: const Icon(Icons.logout),
          )
        ],
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          const Text("home page"),
          Text(hostName),
        ],
      ),
    );
  }
}
