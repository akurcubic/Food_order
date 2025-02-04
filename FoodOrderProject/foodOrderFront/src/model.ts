export interface LoginRequest {
  email: string;
  password: string;
}

export interface User {

  id: number;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  permissions: Permission[];
  role: Role
}

export interface Role {

  id: number;
  role: string;
}

export interface AddUser {

  firstName: string;
  lastName: string;
  email: string;
  password: string;
  permissions: string[];
}

export interface Permission {

  id: number;
  name: string;
}

export interface Order {

  id: number;
  orderStatus: string;
  active: boolean;
  createdDate: string;
  scheduledTime: string;
  user: User;
}

export interface OrderStatusListForSearch {

  listOfStatus: string
}

export interface Dish{

  id: number;
  name: string;
}

export interface CreateOrder {

  order: string;
}

export interface ErrorMessage {

  id: number;
  date: string;
  message: string;
  operations: string;
  order: Order;
  user: User;
}

export interface ScheduleOrder {

  scheduledTime: string;
  dishes: string;
}
