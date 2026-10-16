src/main/java/com/app/ipfinder/
├── Launcher.java
├── IpFinderApplication.java
│
├── config/
│   └── AppConfig.java
│       # HttpClient, ObjectMapper, ExecutorService,
│       # application paths/configuration
│
├── ui/
│   ├── controller/
│   │   ├── MainController.java
│   │   ├── LookupController.java
│   │   └── HistoryController.java
│   └── StageManager.java
│
├── service/
│   ├── IpLookupService.java
│   ├── HistoryService.java
│   └── IpValidator.java
│
├── client/
│   ├── GeoIpClient.java
│   └── IpApiGeoIpClient.java
│
├── storage/
│   ├── HistoryStore.java
│   └── JsonFileHistoryStore.java
│
├── model/
│   ├── IpInfo.java
│   └── LookupEntry.java
│
└── exception/
    ├── IpLookupException.java
    ├── InvalidIpException.java
    ├── GeoIpClientException.java
    └── HistoryStoreException.java         # plain record: ip, country, city, isp, timestamp
