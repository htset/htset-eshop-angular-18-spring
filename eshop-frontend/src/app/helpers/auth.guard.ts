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
      return true;
    }
    else if (currentUser && currentUser.role == 'CUSTOMER') {
      if (route.url.some(segment => segment.path.includes('admin'))) {
        this.router.navigate(['/items']);
      }
      return true;
    }

    this.router.navigate(['/login'],
      { queryParams: { returnUrl: state.url } });
    return false;
  }

}
