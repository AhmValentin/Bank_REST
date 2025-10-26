#  Система управления банковскими картами

##  Описание
REST API для создания и управления банковскими картами с поддержкой ролей **ADMIN** и **USER**, JWT-аутентификацией и возможностью переводов между картами.

---

## Функционал

**ADMIN:**
- Создание, блокировка, активация, удаление карт
- Управление пользователями
- Просмотр всех карт

**USER:**
- Просмотр своих карт (поиск, пагинация)
- Запрос блокировки
- Переводы между своими картами
- Просмотр баланса

---

## Атрибуты карты
- Номер карты (шифруется, отображается как `**** **** **** 1234`)
- Владелец
- Срок действия
- Статус: `ACTIVE`, `BLOCKED`, `EXPIRED`
- Баланс

---

## Технологии
Java 17+, Spring Boot, Spring Security (JWT), Spring Data JPA, PostgreSQL/MySQL, Liquibase, Docker, Swagger, JUnit.

---

###  Эндпоинты

| Метод | URL | Доступ | Описание |
|--------|-----|--------|-----------|
| `POST` | `/auth/login` | Все | Авторизация |
| `POST` | `/auth/register` | Все | Регистрация |
| `GET` | `/cards/admin` | ADMIN | Просмотр всех карт |
| `POST` | `/cards` | ADMIN | Создание новой карты |
| `PATCH` | `/cards/{cardId}/status` | ADMIN | Изменение статуса карты |
| `DELETE` | `/cards/{id}` | ADMIN | Удаление карты |
| `GET` | `/users/**` | ADMIN | Управление пользователями |
| `GET` | `/cards/user` | USER | Просмотр своих карт |
| `POST` | `/cards/{cardId}/block-request` | USER | Запрос блокировки карты |
| `GET` | `/cards/balance` | USER | Просмотр баланса |
| `POST` | `/cards/transfer` | USER | Перевод между своими картами |
| `GET` | `/swagger-ui/**` | Все | Swagger UI |
| `GET` | `/v3/api-docs/**` | Все | OpenAPI документация |

Документация: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Запуск

**Через Docker:**
```bash
git clone https://gitlab.com/AhmValentin/Bank_REST.git
cd bank_rest
docker-compose up -d
