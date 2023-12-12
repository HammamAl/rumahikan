#  User API Spec

## Register User API
Endpoint : POST/api/users

Request Body :

```json
{
    "username"  : "fatan",
    "password"  : "rahasia",
    "nama"      : "fatanfauzan"
}
```

Response Body Success:

```json
{
    "data" : {
        "username"  : "fatan",
        "name"      : "fatanfauzan"
    }
}
```

Response Body Error :
```json
{
    "errors"    : "username salah mas"
}
```

## Login User API

Endpoint : POST/api/users/login

Request Body :
```json
{
    "username"  : "fatan",
    "password"  : "rahasia"
}
```

Response Body Success :
```json
{
    "data" {
        "token" : "unique-token"
    }
}
```

Response Body Error :

```json
{
    "errors" : "ada yang salah nih"
}
```
## Update User ApI

Endpoint : PATCH/api/users/current

Headers :
- Authorization : token

Request Body :
```json
{
    "name"      : "fatanfauzan", //optional
    "password"  : "passbaru" //optional
}
```

Response Body Success :
```json
{
    "data" {
        "username"  : "fatan",
        "name"      : "fatanfauzan"
    }
}
```

Response Body Error :

```json
{
    "errors" : "kepanjangan password atau namanya"
}
```
## Get User API
Endpoint : GET/api/users/current

Headers :
- Authorization : token

Response Body Success:
```json
{
    "data"  : {
        "username"  : "fatan",
        "name"      : "fatanfauzan"
    }
}
```

Response Body Error :

```json
{
    "errors" : "belum masuk nih "
}
```
## Logout User ApI

Endpoint : DELETE/api/users/logout

Headers :
- Authorization : token

Response Body Success:
```json
{
    "data"  : {"OK"}
}
```

Response Body Error :

```json
{
    "errors" : "belum masuk nih "
}
```