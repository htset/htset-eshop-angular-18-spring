import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot }
  from "@angular/router";
import { StoreService } from "../services/store.service";
import { AuthenticationService } from "../services/authentication.service";
import { lastValueFrom } from "rxjs";

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private storeService: StoreService,
    private authenticationService: AuthenticationService
  ) { }

  canActivate(route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot) {
    const currentUser = this.storeService.user;
    if (currentUser && currentUser.role == 'ADMIN') {
      if (currentUser.token && !this.tokenExpired(currentUser.token))
        return true;

      if (!this.refreshToken(currentUser?.token || '',
        currentUser?.refreshToken || '')) {
        this.router.navigate(['/login'],
          { queryParams: { returnUrl: state.url } });
        return false;
      }
      return true;
    }
    else if (currentUser && currentUser.role == 'CUSTOMER') {
      if (currentUser.token && !this.tokenExpired(currentUser.token)) {
        this.router.navigate(['/items']);
        return true;
      }

      if (!this.refreshToken(currentUser?.token || '',
        currentUser?.refreshToken || '')) {
        this.router.navigate(['/login'],
          { queryParams: { returnUrl: state.url } });
        return false;
      }
      return true;
    }

    this.router.navigate(['/login'],
      { queryParams: { returnUrl: state.url } });
    return false;
  }

  private tokenExpired(token: string) {
    const expiry = (JSON.parse(atob(token.split('.')[1]))).exp;
    return (Math.floor((new Date).getTime() / 1000)) >= expiry;
  }

  private async refreshToken(token: string,
    refreshToken: string): Promise<boolean> {
    try {
      this.storeService.isRefreshing = true;
      await lastValueFrom(this.authenticationService
        .refreshToken(token, refreshToken));
      this.storeService.isRefreshing = false;
      return true;
    }
    catch (err) {
      return false;
    }
  }
}
