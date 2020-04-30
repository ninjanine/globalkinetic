import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first, catchError } from 'rxjs/operators';

import { AccountService } from '../../account.service';
import { AlertService } from "../../alert.service";
import { throwError } from 'rxjs';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent implements OnInit {
  form: FormGroup;
  login: string;
  isAddMode: boolean;
  loading = false;
  submitted = false;
  id;
  roles: string[]

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private alertService: AlertService
  ) {}

    ngOnInit() {
      this.login = this.route.snapshot.params['id'];
      this.isAddMode = !this.login;

      const passwordValidators = [Validators.minLength(6)];
      if (this.isAddMode) {
          passwordValidators.push(Validators.required);
      }

      this.form = this.formBuilder.group({
          firstName: ['', Validators.required],
          lastName: ['', Validators.required],
          login: ['', Validators.required],
          password: ['', passwordValidators],
          email: ['', Validators.email],
          phone: ['']
      });

      if (!this.isAddMode) {
          this.accountService.getByLogin(this.login)
              .pipe(first())
              .subscribe(x => {
                this.form.controls.firstName.setValue(x.firstName);
                this.form.controls.lastName.setValue(x.lastName);
                this.form.controls.login.setValue(x.login);
                this.form.controls.email.setValue(x.email);
                this.form.controls.phone.setValue(x.phone);
                this.id = x.id;
                this.roles = x.roles;
              });
      }
    }

    onSubmit() {
      this.submitted = true;
      this.alertService.clear();
      if (this.form.invalid) {
          return;
      }

      this.loading = true;
      if (this.isAddMode) {
          this.createUser();
      } else {
          this.updateUser();
      }
  }

  private createUser() {
    this.accountService.register(this.form.value)
        .pipe(first())
        .subscribe(
            data => {
                this.alertService.success('User added successfully', { keepAfterRouteChange: true });
                this.router.navigate(['.', { relativeTo: this.route }]);
            },
            error => {
                this.alertService.error(error);
                this.loading = false;
            });
  }

  private updateUser() {
      this.accountService.update(this.id, this.roles, this.form.value)
          .pipe(first())
          .subscribe(
              data => {
                  this.alertService.success('Update successful', { keepAfterRouteChange: true });
                  this.router.navigate(['profile', { relativeTo: this.route }]);
              },
              error => {
                  this.alertService.error(error);
                  this.loading = false;
              });
  }

}
