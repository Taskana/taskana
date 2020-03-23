import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { ClassificationEffects } from './effects';
import { classificationReducer } from './reducer';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature('Classification', classificationReducer),
    EffectsModule.forFeature([ClassificationEffects])
  ],
  providers: [ClassificationEffects]
})
export class ClassificationStoreModule {}
