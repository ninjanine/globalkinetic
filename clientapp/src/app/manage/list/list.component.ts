import { Component, OnInit } from '@angular/core';
import { first } from 'rxjs/operators';
import { AccountService } from '../../account.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  users = null;

  constructor(private accountService: AccountService) {}

  ngOnInit() {
    this.accountService.getAll()
        .pipe(first())
        .subscribe(users => this.users = users);
  }

  deleteUser(login: string) {
    const user = this.users.find(x => x.login === login);
    user.isDeleting = true;
    this.accountService.delete(login)
        .pipe(first())
        .subscribe(() => {
            this.users = this.users.filter(x => x.login !== login)
        });
  }

}
