import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {CreateOrder, Dish, ErrorMessage, Order, OrderStatusListForSearch, ScheduleOrder} from "../../model";
import {environment} from "../../enviroments/environment.development";

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({

      Authorization: `Bearer ${localStorage.getItem('token')}`
    })
  }

  constructor(private http: HttpClient) { }

  getAllOrders(): Observable<Order[]> {

    return this.http.get<Order[]>(environment.orderService, {headers: this.getAuthHeaders()});
  }

  getOrdersForUser(id: number): Observable<Order[]> {

    return this.http.get<Order[]>(environment.orderService+"/"+id, {headers: this.getAuthHeaders()});
  }

  ordersByUserId(id: string): Observable<Order[]> {

    return this.http.get<Order[]>(environment.orderService+"/search_by_user/"+id, {headers: this.getAuthHeaders()});
  }

  ordersByStatus(orderStatusList: OrderStatusListForSearch): Observable<Order[]> {

    return this.http.post<Order[]>(environment.orderService+"/search_by_status", orderStatusList, {headers: this.getAuthHeaders()});
  }

  ordersBetweenDates(startDate: string, endDate: string): Observable<Order[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<Order[]>(`${environment.orderService}/search_by_dates/`, {
      headers: this.getAuthHeaders(),
      params: params
    });
  }

  getDishes(): Observable<Dish[]> {

    return this.http.get<Dish[]>(environment.orderService+"/dishes", {headers: this.getAuthHeaders()});
  }

  createOrder(order: CreateOrder): Observable<Order> {

    return this.http.post<Order>(environment.orderService, order, {headers: this.getAuthHeaders()});
  }

  // POST mora uvek da ima telo zahteva inace ce se dobiti 403 Forbiden

  cancelOrder(id: number): Observable<Order> {
    return this.http.post<Order>(
      `${environment.orderService}/cancel_order/${id}`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  getAllErrors(page: number, size: number): Observable<ErrorMessage[]> {

    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<ErrorMessage[]>(environment.orderService+"/all_errors", {
      headers: this.getAuthHeaders(),
      params: params
    });
  }

  getErrorsForUser(userId: number, page: number, size: number): Observable<ErrorMessage[]> {

    const params = new HttpParams()
      .set('userId', userId)
      .set('page', page)
      .set('size', size);

    return this.http.get<ErrorMessage[]>(environment.orderService+"/user_errors", {
      headers: this.getAuthHeaders(),
      params: params
    });
  }

  scheduleOrder(order: ScheduleOrder): Observable<Order> {

    return this.http.post<Order>(environment.orderService+"/schedule", order, {headers: this.getAuthHeaders()});
  }
}
