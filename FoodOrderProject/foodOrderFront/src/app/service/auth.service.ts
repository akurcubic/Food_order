import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LoginRequest} from "../../model";
import {Observable} from "rxjs";
import {environment} from "../../enviroments/environment.development";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(loginRequest: LoginRequest): Observable<any> {

      return this.http.post<any>(environment.authService, loginRequest);
  }
}
