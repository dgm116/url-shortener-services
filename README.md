# URL Shortener Services

## Description
This project provides an API that allows creating short links from longer ones. The short links generated can be used to redirect to the original URL using another API endpoint.

## Features
- Shorten a URL and return a short link.
- Redirect from a short link to the original URL.

## Endpoints

### 1. Create a Short Link
**Endpoint**: `POST /st1/shorten`

**Description**: Creates a short link from a given URL.

**Request Parameters**:
- `url` (String): The original URL to be shortened.

**Sample Request**:
```bash
curl -X POST "http://localhost:8081/st1/shorten?url=https://example.com/long-url"
```
**Response**:
- Status Code: `201 created` 
- Response Body:
```json
{
  "shortUrl": "http://short.ly/abc123"
}
```
### 2. Redirect to the Original URL
**Endpoint**: `GET /st1/{id}`

**Description**: Redirects the user to the original URL based on the given identifier.

**Request Parameters**:
- `id` (String): The identifier of the short URL.

**Sample Request**:
```bash
curl -X GET "http://localhost:8081/st1/abc123"
```
**Response**:
- Status Code: `302 Found` if the URL is found and redirects the browser to the original URL.
- Status Code: `404 Not Found` if the corresponding URL is not found.

## Technologies Used
- Java 17 
- Spring Boot 3.3.5
- Maven 3.9.9
- Firebase

## Set Up and Run
### 1. Clone Repository
```bash
git clone https://github.com/dgm116/url-shortener-services
cd url-shortener-services
```
### 2. Build the Project 
```bash
mvn clean install
```
### 3. Run Application 
```bash
mvn spring-boot:run
```
## Important
You have to ensure that there is a file serviceAccount.json into resources path, firestore DataBase
