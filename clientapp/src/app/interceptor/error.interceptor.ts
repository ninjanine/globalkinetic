import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';


import { AccountService } from '../account.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private accountService: AccountService,
                private router: Router) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError(err => {
            if (err.status === 401 || err.status === 400) {
                this.accountService.logout();
            } else if (err.status === 404 || err.status === 403) {
                this.router.navigate(['/denied']);
            }

            if (err.error) {
                const error = err.error.message || err.statusText;
                return throwError(error);
            }
        }))
    }
}