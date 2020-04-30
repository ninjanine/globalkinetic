import { Component, OnInit } from '@angular/core';
import { first } from 'rxjs/operators';
import { AccountService } from '../../account.service';

@Component({
  selector: 'app-sessions',
  templateUrl: './sessions.component.html',
  styleUrls: ['./sessions.component.scss']
})
export class SessionsComponent implements OnInit {
  sessions = null;

  constructor(private accountService: AccountService) {
    console.log(this.sessions);
  }

  ngOnInit() {
    this.accountService.getSessions()
        .pipe(first())
        .subscribe(sessions => this.sessions = sessions);
}

}
