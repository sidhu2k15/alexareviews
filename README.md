# Alexa Reviews Service  

Alexa Reviews Service is a RESTful API that manages and analyzes Amazon Alexa app reviews from Google Play Store and Apple App Store.  

## Requirements  

- **MySQL 8+** (or H2 for local testing)  
- **JDK 17**  
- **Gradle 6.8+**  

## Installation  

### 1. Clean and Build the Project  
```bash
cd {project_root_directory}
gradle clean
gradle buildAll
```  

### 2. Get the Distribution Zip File  
```bash
cd {project_root_directory}/build/distributions/alexa-reviews-x.x.x.zip
```  

### 3. Deploy and Run  
```bash
unzip alexa-reviews-x.x.x.zip
java -jar alexa-reviews-x.x.x.jar
```  

## API Endpoints  

### 1. Add a Review  
**POST** `/v1/api/reviews`  
- **Request Body:**  
```json
{
  "review": "Great app, very useful!",
  "author": "John Doe",
  "reviewSource": "Google Play",
  "rating": 5,
  "title": "Awesome App",
  "productName": "Alexa",
  "reviewedDate": "2024-02-01"
}
```

### 2. Fetch Reviews with Filters  
**GET** `/v1/api/reviews?startDate=2024-01-01&endDate=2024-01-31&storeType=Google Play&rating=5`  

### 3. Get Monthly Average Ratings  
**GET** `/v1/api/reviews/monthly-average`  

### 4. Get Total Ratings by Category  
**GET** `/v1/api/reviews/total-ratings`  

## Swagger Documentation  
Once the service is running, access Swagger UI:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  

## Database Migration (Flyway)  
The project uses **Flyway** for database versioning. Migrations are stored in `config/database/scripts`.  
To run migrations manually:  
```bash
gradle flywayMigrate
```

## Running Tests  
Execute unit tests using:  
```bash
gradle test
```

## Logging and Monitoring  
Logs are stored in `logs/` directory and can be customized via `application.properties`.  
To view logs in real-time:  
```bash
tail -f logs/app.log
```

## Contribution  
Feel free to submit pull requests and raise issues to improve this service.
