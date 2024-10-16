import { Injectable } from "@angular/core";
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest }
  from "@angular/common/http";
import { BehaviorSubject, Observable, catchError, filter, switchMap, take, throwError } from "rxjs";
import { environment } from "../../environments/environment";
import { StoreService } from "../services/store.service";
import { Router } from "@angular/router";
import { AuthenticationService } from "../services/authentication.service";
import { User } from "../models/user";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<string | null>
    = new BehaviorSubject<string | null>(null);

  constructor(private router: Router,
    private storeService: StoreService,
    private authenticationService: AuthenticationService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler):
    Observable<HttpEvent<any>> {

    const currentUser = this.storeService.user;
    const isLoggedIn = currentUser && currentUser.token;
    const isApiUrl = request.url.startsWith(environment.apiUrl);
    if (isLoggedIn && isApiUrl) {

      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${currentUser?.token}`
        }
      });

      return next.handle(request).pipe(
        catchError(error => {
          // If 401 Unauthorized error, try to refresh the token
          if (error.status === 401 && !this.isRefreshing) {
            return this.handle401Error(request, next);
          }
          return throwError(() => error);
        })
      );
    }
    return next.handle(request);
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const currentUser = this.storeService.user;
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.authenticationService.refreshToken(currentUser?.token || '',
        currentUser?.refreshToken || '').pipe(
          switchMap((newUser: User) => {
            this.isRefreshing = false;
            this.refreshTokenSubject.next(newUser.token || '');
            return next.handle(request = request.clone({
              setHeaders: {
                Authorization: `Bearer ${newUser?.token}`
              }
            }));
          }),
          catchError((err) => {
            this.isRefreshing = false;
            this.authenticationService.logout(currentUser?.refreshToken || '');
            return throwError(() => err);
          })
        );
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token !== null),
        take(1),
        switchMap(token => {
          return next.handle(request = request.clone({
            setHeaders: {
              Authorization: `Bearer ${currentUser?.token}`
            }
          }));
        })
      );
    }
  }
}
