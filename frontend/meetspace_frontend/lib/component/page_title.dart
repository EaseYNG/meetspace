import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class PageTitle extends StatelessWidget {
  final String text;
  final EdgeInsets? padding;

  const PageTitle({super.key, required this.text, this.padding});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: padding ?? EdgeInsets.fromLTRB(25, 100, 200, 25),
      child: Text(
        text,
        style: GoogleFonts.inter(fontSize: 40, fontWeight: FontWeight.normal),
      ),
    );
  }
}
