import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminOrdersComponent } from './admin-orders.component';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap';
import { ApolloTestingModule } from 'apollo-angular/testing';

describe('AdminOrdersComponent', () => {
  let component: AdminOrdersComponent;
  let fixture: ComponentFixture<AdminOrdersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminOrdersComponent],
      imports: [RouterModule.forRoot([]),
        FormsModule,
        NgbPagination,
        ApolloTestingModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AdminOrdersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
