import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from "../environments/environment";
import { User } from './models/user';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

    private userSubject: BehaviorSubject<User>;
    public user: Observable<User>;

    constructor(private router: Router,
                private http: HttpClient) {
            this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('user')));
            this.user = this.userSubject.asObservable();
    }

    public get userValue(): User {
        return this.userSubject.value;
    }

    login(username, password) {
        return this.http.post<User>(`${environment.apiUrl}/login`, { username, password })
            .pipe(map(user => {
                localStorage.setItem('user', JSON.stringify(user));
                this.userSubject.next(user);
                return user;
            }));
    }

    logout() {
        if (!this.userSubject.value) {
            localStorage.removeItem('user');
            this.router.navigate(['/account/login']);
            return;
        }

        const login = this.userSubject.value.login
        this.http.get(`${environment.apiUrl}/logout/${login}`)
        .subscribe(data =>{
            localStorage.removeItem('user');
            this.userSubject.next(null);
            this.router.navigate(['/account/login']);
        })
    }

    register(user: User) {
        return this.http.post(`${environment.apiUrl}/users`, user);
    }

    getAll() {
        return this.http.get<User[]>(`${environment.apiUrl}/users`);
    }

    getSessions() {
        return this.http.get<User[]>(`${environment.apiUrl}/loggedUsers`);
    }

    getByLogin(login: string) {
        return this.http.get<User>(`${environment.apiUrl}/users/${login}`);
    }

    update(id, roles, user: User) {
        user.id = id;
        user.roles = roles;
        return this.http.put(`${environment.apiUrl}/users`, user)
            .pipe(map(x => {
                if (user.login == this.userValue.login) {
                    user.token = this.userValue.token;
                    localStorage.setItem('user', JSON.stringify(user));
                    this.userSubject.next(user);
                }
                return x;
            }));
    }

    delete(login: string) {
        return this.http.delete(`${environment.apiUrl}/users/${login}`)
            .pipe(map(x => {
                // auto logout if the logged in user deleted their own record
                if (login == this.userValue.login) {
                    this.logout();
                }
                return x;
            }));
    }
}
