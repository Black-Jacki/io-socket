import 'package:flutter/material.dart';

class ListItem extends StatelessWidget {
  
  final String content;
  
  final bool isOwner;
  
  const ListItem({Key? key, required this.content, required this.isOwner}) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    
    EdgeInsetsGeometry margin = isOwner
        ? const EdgeInsets.only(left: 50, right: 6, top: 6, bottom: 6)
        : const EdgeInsets.only(left: 6, right: 50, top: 6, bottom: 6);
    Color bgColor = isOwner ? const Color(0xff91caff) : Colors.white;
    
    return Container(
      padding: const EdgeInsets.all(12),
      margin: margin,
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: const BorderRadius.all(Radius.circular(6)),
      ),
      child: Text(content),
    );
  }
}