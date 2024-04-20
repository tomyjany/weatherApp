import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComponentTestComponent } from './component-test.component';

describe('ComponentTestComponent', () => {
  let component: ComponentTestComponent;
  let fixture: ComponentFixture<ComponentTestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComponentTestComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ComponentTestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
