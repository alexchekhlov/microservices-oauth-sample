Simple example of microservices application

System consists of 3 microservices:

1- responsible for user management + oauth authentication

2- responsible for meals management and settings related to calories

3- gateway

Next steps would be:

-Add Logging

-Enhance filters to add operations and fix some limitations

-Update communication between services to use some MQ

-Finish unit tests

-Add testing of endpoints security

-Add Spring Cloud configuration and discovery server

-Create common parent maven artifact to not duplicate dependencies

-Some more refactoring to avoid duplicate code

-Continue adding swagger documentation, combine documentation from different services into one using eureka

-Enhance hystrix to use cache

