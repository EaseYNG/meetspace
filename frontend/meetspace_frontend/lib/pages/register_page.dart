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

  void _click() {
    print("clicked");
  }

  void _submit() {
    print("submit");
    _register();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.blue,
        foregroundColor: Colors.amber,
      ),
      body: Center(
        child: Column(
          children: [
            const Text("Register:"),
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
      bottomNavigationBar: BottomAppBar(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          spacing: 4,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              spacing: 16,
              children: [
                ElevatedButton(onPressed: _click, child: Text('1')),
                ElevatedButton(onPressed: _click, child: Text('2')),
                ElevatedButton(onPressed: _click, child: Text('3')),
                ElevatedButton(onPressed: _click, child: Text('4')),
              ],
            ),
            const Text("meetspace v1.0")
          ],
        )
      ),
    );
  }
}
