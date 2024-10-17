import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Order } from '../models/order';
import { Apollo, gql } from 'apollo-angular';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  constructor(private http: HttpClient,
    private apollo: Apollo) { }

  addOrder(order: Order) {
    return this.http
      .post<Order>(`${environment.apiUrl}/orders`, order);
  }

  getOrders(page: number, pageSize: number, search?: string): Observable<any> {
    return this.apollo.query({
      query: gql`
        query getOrders($page: Int, $pageSize: Int, $search: String) {
          getOrders(page: $page, pageSize: $pageSize, search: $search) {
            totalCount
            orders{
              id
              userId
              orderDate
              totalPrice
              firstName
              lastName
            }
          }
        }
      `,
      variables: {
        page,
        pageSize,
        search: search || null, // Pass null if no search term is provided
      },
    });
  }

  getOrder(orderId: number): Observable<Order> {
    return this.http
      .get<Order>(`${environment.apiUrl}/orders/${orderId}`);
  }
}
