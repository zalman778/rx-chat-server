## Реактивый чат, серверная часть.

Используются технологии:
* spring boot
* spring security
* jwt token
* reactive mongo repository
* hibernate
* rsocket

Все реактивные события оборачиваются в инстанс RxObject с различными свойствами.
Есть реакивные сущности для MongoDB: RxMessage, RxEvent.
И обычные сущности-енити БД: Dialog, Message, UserEntity.

## Требования

Создать следующие проперти в config/external.properties:
* server.context.upload.path=<путь к upload folder>
* spring.datasource.url=
* spring.datasource.username=
* spring.datasource.password=
* spring.data.mongodb.host=
* spring.data.mongodb.port=
* spring.data.mongodb.database=
* server.ssl.key-store=classpath:config/
* server.ssl.key-store-password=
* server.ssl.key-alias=

Добавить keystore.

У монго должна быть настроена реплика.

