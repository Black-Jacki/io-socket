class Message {
  
  final String address;
  
  final String port;

  final String hostName;
  
  final String content;
  
  final bool isOwner;
  
  Message({
    required this.address, 
    required this.port,
    required this.hostName,
    required this.isOwner,
    required this.content,
  });
  
}