import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { ClassificationStoreModule } from './classification-store';
import { environment } from '../../../environments/environment'; // Angular CLI environment

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ClassificationStoreModule,
    StoreModule.forRoot({}),
    EffectsModule.forRoot([]),
    !environment.production ? StoreDevtoolsModule.instrument({
      maxAge: 25, // Retains last 25 states
    }) : [],
  ]
})
export class AdministrationStoreModule { }
