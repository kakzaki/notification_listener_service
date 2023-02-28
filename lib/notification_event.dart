class ServiceNotificationEvent {
  /// the notification id
  int? id;

  /// notification package name
  String? packageName;

  /// notification title
  String? title;

  /// the content of the notification
  String? content;

  ServiceNotificationEvent({
    this.id,
    this.packageName,
    this.title,
    this.content,
  });

  ServiceNotificationEvent.fromMap(Map<dynamic, dynamic> map) {
    id = map['id'];
    packageName = map['packageName'];
    title = map['title'];
    content = map['content'];
  }

  @override
  String toString() {
    return '''ServiceNotificationEvent(
      id: $id
      packageName: $packageName
      title: $title
      content: $content
      ''';
  }
}
