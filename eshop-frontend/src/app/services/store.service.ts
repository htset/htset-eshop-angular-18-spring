import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { Item } from '../models/item';
import { Filter } from '../models/filter';
import { Cart } from '../models/cart';
import { User } from '../models/user';
import { Order } from '../models/order';

@Injectable({
  providedIn: 'root'
})
export class StoreService {

  private readonly _items = new BehaviorSubject<Item[]>([]);
  readonly items$ = this._items.asObservable();

  get items(): Item[] {
    return this._items.getValue();
  }

  set items(val: Item[]) {
    this._items.next(val);
  }

  private readonly _page = new BehaviorSubject<number>(1);
  readonly page$ = this._page.asObservable();

  get page(): number {
    return this._page.getValue();
  }

  set page(val: number) {
    this._page.next(val);
  }

  public pageSize: number = 3;
  public readonly _pageSizeSubject = new Subject<number>();
  public pageSizeChanges$ = this._pageSizeSubject.asObservable();

  private readonly _count = new BehaviorSubject<number>(1);
  readonly count$ = this._count.asObservable();

  get count(): number {
    return this._count.getValue();
  }

  set count(val: number) {
    this._count.next(val);
  }

  private readonly _filter = new BehaviorSubject<Filter>({ name: "", categories: [] });
  readonly filter$ = this._filter.asObservable();

  get filter(): Filter {
    return this._filter.getValue();
  }

  set filter(val: Filter) {
    this._filter.next(val);
  }

  private readonly _cart =
    new BehaviorSubject<Cart>(new Cart(localStorage.getItem('cart') || ''));
  readonly cart$ = this._cart.asObservable();

  get cart(): Cart {
    return this._cart.getValue();
  }

  set cart(val: Cart) {
    this._cart.next(val);
  }


  private readonly _user
    = new BehaviorSubject<User | null>(
      (sessionStorage.getItem('user') === null) ?
        null : JSON.parse(sessionStorage.getItem('user') ?? "")
    );
  readonly user$ = this._user.asObservable();

  get user(): User | null {
    return this._user.getValue();
  }

  set user(val: User | null) {
    this._user.next(val);
  }

  private readonly _deliveryAddress = new BehaviorSubject<number>(-1);
  readonly deliveryAddress$ = this._deliveryAddress.asObservable();

  get deliveryAddress(): number {
    return this._deliveryAddress.getValue();
  }

  set deliveryAddress(val: number) {
    this._deliveryAddress.next(val);
  }

  private readonly _order = new BehaviorSubject<Order>(new Order());
  readonly order$ = this._order.asObservable();

  get order(): Order {
    return this._order.getValue();
  }

  set order(val: Order) {
    this._order.next(val);
  }

  private readonly _orders = new BehaviorSubject<Order[]>([]);
  readonly orders$ = this._orders.asObservable();

  get orders(): Order[] {
    return this._orders.getValue();
  }

  set orders(val: Order[]) {
    this._orders.next(val);
  }

  private readonly _orderPage = new BehaviorSubject<number>(1);
  readonly orderPage$ = this._orderPage.asObservable();

  get orderPage(): number {
    return this._orderPage.getValue();
  }

  set orderPage(val: number) {
    this._orderPage.next(val);
  }

  public orderPageSize: number = 3;
  public readonly _orderPageSizeSubject = new Subject<number>();
  public orderPageSizeChanges$ = this._orderPageSizeSubject.asObservable();

  private readonly _orderCount = new BehaviorSubject<number>(1);
  readonly orderCount$ = this._orderCount.asObservable();

  get orderCount(): number {
    return this._orderCount.getValue();
  }

  set orderCount(val: number) {
    this._orderCount.next(val);
  }

  constructor() { }
}
