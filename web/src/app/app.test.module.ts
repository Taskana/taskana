import { NgModule } from '@angular/core';
import { TaskanaTypeAheadMockComponent } from 'app/shared/type-ahead/type-ahead.mock.component';

const MODULES = [
];

const DECLARATIONS = [
    TaskanaTypeAheadMockComponent
];

@NgModule({
    declarations: DECLARATIONS,
    imports: MODULES,
    providers: []
})
export class AppTestModule {
}
