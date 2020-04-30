import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { ManageRoutingModule } from './manage-routing.module';
import { LayoutComponent } from './layout/layout.component';
import { ListComponent } from './list/list.component';
import { EditComponent } from './edit/edit.component';
import { SessionsComponent } from './sessions/sessions.component';

@NgModule({
  declarations: [
    LayoutComponent,
    ListComponent,
    EditComponent,
    EditComponent,
    SessionsComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ManageRoutingModule
  ]
})
export class ManageModule { }
