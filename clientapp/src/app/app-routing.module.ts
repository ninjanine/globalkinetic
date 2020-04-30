import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ProfileComponent } from "./profile/profile.component";
import { DeniedComponent } from "./denied/denied.component";
import { AuthGuard } from './auth.guard';

const accountModule = () => import('./account/account.module').then(x => x.AccountModule);
const manageModule = () => import('./manage/manage.module').then(x => x.ManageModule);

const routes: Routes = [
  { path: '', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'manage', loadChildren: manageModule, canActivate: [AuthGuard] },
  { path: 'account', loadChildren: accountModule },
  { path: 'denied', component: DeniedComponent},
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
