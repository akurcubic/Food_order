# RAF Food Ordering System  

## Project Overview  
The **RAF Food Ordering System** is a web application designed for food ordering process. The application allows users to place orders, track their status in real-time, and schedule orders for future delivery. Additionally, it provides administrative functionalities for managing orders and users.  

## Key Features  

- **User Management** – Users can register, log in, and manage their profiles. Permissions determine what actions they can perform within the system.  
- **Order Management** – Users can create new orders, view their order history, cancel pending orders, and track their current order status.  
- **Order Processing & Status Updates** – Orders transition through different statuses (`ORDERED`, `PREPARING`, `IN_DELIVERY`, `DELIVERED`), with automatic updates handled in the background.  
- **Scheduled Orders** – Users can schedule an order for a specific time, and the system will automatically process it. If an order cannot be fulfilled due to system constraints, an error log is recorded.  
- **Permissions & Role-Based Access** – Specific permissions control user access to various actions, ensuring that only authorized users can perform administrative tasks.  
- **Real-Time Order Tracking** – Order statuses update dynamically using WebSockets, providing users with live updates without manual page refreshes.  
- **Error Logging & Reporting** – Errors related to scheduled orders (such as exceeding the maximum number of simultaneous orders) are recorded and available for review by users and administrators.  
- **Search & Filtering** – Users can search and filter orders based on status, creation date, and other parameters. Administrators have access to all users’ orders, while regular users can only view their own.  
- **Frontend Implementation** – A responsive user interface that allows users to interact with the system efficiently, including order creation, tracking, and searching functionalities.  

## Technologies Used  

- **Backend:** Java (`Spring Boot`), relational database for data storage.  
- **Frontend:** `Angular` for an interactive user experience.  

