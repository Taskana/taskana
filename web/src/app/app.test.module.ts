import { NgModule } from '@angular/core';
import { KadaiTypeAheadMockComponent } from 'app/shared/components/type-ahead/type-ahead.mock.component';

const MODULES = [];

const DECLARATIONS = [KadaiTypeAheadMockComponent];

@NgModule({
  declarations: DECLARATIONS,
  imports: MODULES,
  providers: []
})
export class AppTestModule {}
