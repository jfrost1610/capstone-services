# spring-boot-301-restaurant-project
This project contains configuration and microservices for restaurant project case study for spring boot 301.

It contains the following components:

## restaurantsearchservice

Microservice which provides provision to search restaurants by name, cuisine, location, budget, rating, by item in the menu of restaurant or by restaurant ID.

## orderingservice

Microservice which provides provision to place order for a customer, update the order, get order details by ID, or cancel the order.

## config-server

Spring cloud config server based microservice which provides provision to fetch configuration stored in github for all other microservices.

## restaurant-config

Configuration properties needed by Restaurant services, if switched to config server later.


**For more information on each of these components, please refer to README.md files of these component folders.**
