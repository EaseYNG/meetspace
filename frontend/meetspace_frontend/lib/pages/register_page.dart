import 'package:flutter/material.dart';

class RegisterPage extends StatefulWidget {
  const RegisterPage({super.key});

  final String text = '注册';

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final _textController = TextEditingController();
  final _passwordController = TextEditingController();

  void _register() async {
    String username = _textController.text;
    String password = _passwordController.text;

    // 调用api
  }

  void _submit() {
    print("submit");
    _register();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        child: Column(
          children: [
            TextFormField(
              controller: _textController,
              decoration: InputDecoration(hintText: "Enter username: "),
              validator: (String? value) {
                if (value == null || value.isEmpty) {
                  return 'Please Enter Username!';
                }
                return null;
              },
            ),
            TextFormField(
              controller: _passwordController,
              obscureText: true,
              decoration: InputDecoration(hintText: "Enter password: "),
              validator: (String? value) {
                if (value == null || value.isEmpty) {
                  return 'Please Enter Password!';
                }
                return null;
              },
            ),
            FloatingActionButton(onPressed: _submit, child: const Text("提交")),
          ],
        ),
      ),
    );
  }
}
