import { Injectable } from "@angular/core";
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest }
  from "@angular/common/http";
import { Observable, filter, switchMap, take } from "rxjs";
import { environment } from "../../environments/environment";
import { StoreService } from "../services/store.service";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private storeService: StoreService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler):
    Observable<HttpEvent<any>> {

    console.log(this.storeService.isRefreshing);
    if (this.storeService.isRefreshing == false
      || request.url.indexOf('refresh') >= 0) {
      //If we are not refreshing, go ahead with the request
      const currentUser = this.storeService.user;
      const isLoggedIn = currentUser && currentUser.token;
      const isApiUrl = request.url.startsWith(environment.apiUrl);
      if (isLoggedIn && isApiUrl) {
        request = request.clone({
          setHeaders: {
            Authorization: `Bearer ${currentUser?.token}`
          }
        });
      }
      return next.handle(request);
    }
    else {
      //If we are refreshing, wait until we finish it
      return this.storeService.isRefreshing$.pipe(
        filter(isRefreshing => !isRefreshing), //Wait until isRefreshing becomes false
        take(1), //Take only one value after it becomes false
        switchMap(() => {
          //If the token was refreshed, update the request with the new token
          const updatedUser = this.storeService.user;
          request = request.clone({
            setHeaders: {
              Authorization: `Bearer ${updatedUser?.token}`
            }
          });
          //Retry the request with the updated token
          return next.handle(request);
        })
      );
    }
  }
}
