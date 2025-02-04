import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {AddUser, User} from "../../model";
import {environment} from "../../enviroments/environment.development";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({

      Authorization: `Bearer ${localStorage.getItem('token')}`
    })
  }

  constructor(private http: HttpClient) { }

  getUserByEmail(email: string): Observable<User> {

      return this.http.get<User>(environment.userService+"/email/"+email, {headers: this.getAuthHeaders()});
  }

  getUserById(id: Number): Observable<User> {

    return this.http.get<User>(environment.userService+"/"+id, {headers: this.getAuthHeaders()});
  }

  getAllUsers(): Observable<User[]>{

    return this.http.get<User[]>(environment.userService, {headers: this.getAuthHeaders()})
  }

  deleteUser(id: number): Observable<any>{

    return this.http.delete<any>(environment.userService+"/"+id, {headers: this.getAuthHeaders()})
  }

  addUser(user: AddUser):Observable<User>{

    return this.http.post<User>(environment.userService, user, {headers: this.getAuthHeaders()})
  }

  editUser(user: AddUser,id : Number):Observable<User>{

    return this.http.put<User>(environment.userService+"/"+id, user, {headers: this.getAuthHeaders()})
  }
}
