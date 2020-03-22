# 함수

## 용어 정리
- 기명 함수 표현식
```
var add = function add(a, b) {
    return a + b;
};
```

- 무명 함수 표현식 (익명 함수)
```
var add = function (a, b) {
    return a + b;
};
```

- 함수 선언문
```
function add() {
    return a + b;
}
```

---

## 호이스팅
모든 변수는 함수 본문 어느 부분에서 선언되더라도 내부적으로 함수의 맨 윗부분으로 끌어올려진다. 함수 선언문을 사용하면 변수 선언 뿐만 아니라 함수 정의 자체도 호이스팅 되기 때문에 다음과 같이 자칫 오류를 만들 수 있다.
```
function foo() {
    alert("global foo");
}

function bar() {
    alert("global bar");
}

function hoiseMe() {
    console.log(typeof foo); //function
    console.log(typeof bar); //undefined

    foo(); //local foo
    bar(); //TypeError: bar is not a function

    function foo() {
        alert("local foo");
    }

    var bar = function () {
        alert("local bar");
    };
}
```
`hoistMe()` 내에서 foo와 bar를 정의하면 실제 변수를 정의한 위치와 상관없이 끌어올려져 전역 변수인 foo와 bar를 덮어쓴다. 그런데 bar()의 정의는 호이스팅되지 않고 선언문만 호이스팅 되었으므로, bar() 정의가 나오기 전까지 undefined 상태이다. 또한 bar() 선언문 자체는 호이스팅 되었기 때문에 유효범위 체인 내에서 전역 bar()도 찾을 수 없다.

## 콜백 패턴
### 콜백과 유효범위
콜백이 일회성의 익명 함수나 전역 함수가 아니고 객체의 메서드인 경우도 많다. 만약 콜백 메서드가 자신이 속해있는 객체를 참조하기 위해 `this`를 사용하면 예상대로 동작하지 않을 수 있다.

```
var app = {};
app.color = "green";
app.paint = function (node) {
    node.style.color = this.color;
};

var findNodes = function (callback) {
    // ...
    if (typeof callback === "function") {
        callback(found);
    }
    // ...
};
```
여기서 `findNodes(app.paint)`를 호출하면? `this.color`가 정의되지 않아 예상대로 동작하지 않는다. `findNodes()`가 전역함수라 `this`는 전역 객체를 참조한다. `findNodes()`가 dom이란 객체의 메서라라면 콜백 내부의 `this`는 `app`이 아닌 dom을 참조하게 된다. 이를 해결하려면 콜백함수와, 콜백이 속한 객체를 함께 전달하면 된다.  
```
findNodes(app.paint, app);

var findNodes = function (callback, callback_obj) {
    // ...
    if (typeof callback === "function") {
        callback.call(callback_obj, found);
    }
    // ...
};
```

## 함수 반환
함수는 객체이기 때문에 반환 값으로 사용될 수 있다.
```
var setup = function () {
    var count = 0;
    return function () {
        return (count + 1);
    };
};

var next = setup();
next(); //1
next(); //2
next(); //3
```
setup()은 반환된 함수를 감싸 클로저를 생성한다. 클로저는 반환되는 함수에서는 접근할 수 있지만 코드 외부에서는 접근할 수 없다.

## 자기 자신을 정의하는 함수
```
var scareMe = function () {
    alert("Boo!");
    scareMe = function () {
        alert("Boo 2!");
    };
};

scareMe(); //Boo!
scareMe(); //Boo 2!
```
함수가 어떤 초기화 준비 작업을 딱 한 번만 수행할 경우에 사용할 수 있다. 함수의 일부가 더 이상 쓸모가 없기때문에 함수가 자기 자신을 재정의하여 구현 내용을 갱신할 수 있다.  
하지만 자기 자신을 재정의한 이후에는 이전의 원본 함수에 추가했던 프로퍼티들을 모두 찾을 수 없다는 단점이 있다. 또한 함수가 다른 이름으로 사용된다면(다른 변수에 할당되거나 객체의 메서드로 사용되면), 재정의된 부분이 아니라 원본 함수가 실행된다.

```
// 새로운 프로퍼티 추가
scareMe.property = "test";

// 다른 이름으로 할당
var anotherName = scareMe;

// 메서드로 사용
var obj = {
    boo: scareMe
};

// 새로운 이름으로 호출
anotherName(); //Boo!
anotherName(); //Boo! : scareMe가 재정의되었고 anotherName은 재정의되지 않았다
console.log(anotherName.property); //test

// 메서드로 호출한다
obj.boo(); //Boo!
obj.boo(); //Boo! : scareMe가 재정의되었고 obj.boo는 재정의되지 않았다
console.log(obj.boo.property); //test

// 자기 자신을 재정의한 함수를 사용
scareMe(); //Boo 2! : anotherName()을 처음 호출한 순간 scareMe가 재정의되었다
scareMe(); //Boo 2!
console.log(scareMe.property); //undefined : scareMe가 재정의되면서 프로퍼티가 날라갔다
```

## 즉시 실행 함수
함수를 선언하자마자 실행한다.
```
(function () {
    alert("Hello");
}());
```
단 한 번만 실행되는 초기화를 위해 전역 변수를 생성할 필요는 없다. 대신 즉시 실행 함수를 사용할 수 있다. 즉시 실행 함수는 모든 코드를 지역 유효범위로 감싼다.

### 즉시 실행 함수의 매개변수
```
(function (who, when) {
    console.log("I met " + who + " on " + when);
}("John", new Date()));
```
일반적으로 전역 객체가 즉시 실행 함수의 인자로 전달되어 즉시 실행 함수 내에서 `window`를 사용하지 않고도 전역 객체에 접근할 수 있다.
```
(function (global) {
    // ... 전역 객체를 global로 참조
}(this));
```
즉시 실행 함수에 대한 인자를 너무 많이 전달하지 않는 것이 좋다. 코드 윗부분과 아랫부분을 오가며 코드를 확인해야 하기 때문이다.

### 즉시 실행 함수의 반환값
```
var result = (function () {
    return 2 + 2;
}());
```
원시 데이터 값 외에도 모든 데이터 값을 반환할 수 있다.
```
var getResult = (function () {
    var res = 2 + 2;
    return function () {
        return res;
    };
}());
```
즉시 실행 함수의 유효 범위를 사용해 `res`를 비공개 상태로 클로저에 저장하고 반환되는 함수에서만 접근하도록 했다.

다음과 같이 객체 프로퍼티를 정의할 때도 사용할 수 있다. 어떤 객체의 프로퍼티가 객체의 생명주기 동안에는 값이 변하지 않고, 처음에 값을 정의할 때는 적절한 계산을 위한 작업이 필요하다면, 이 작업을 즉시 실행 함수로 감싼 후 즉시 실행 함수의 반환 값을 프로퍼티 값으로 할당하면 된다.
```
var o = {
    message: (function () {
        var who = "me",
            what = "call";
        return what + " " + who;
    }()),
    getMessage: function () {
        return this.message;
    }
};

o.getMessage(); //call me
o.messgae; //call me
```

## 즉시 객체 초기화
전역 유효범위를 어지럽히지 않도록 하는 또 다른 방법이다. 객체가 생성된 즉시 init() 메서드를 실행해 객체를 사용한다. init() 함수는 모든 초기화 작업을 처리한다.
```
({
    maxWidth: 600,
    maxHeight: 400,
    getMax: function () {
        return this.maxWidth + " x " + this.maxHeight;
    },
    init: function () {
        console.log(this.getMax());
    }
}).init();
```
초기화 작업이 복잡하다면 즉시 실행 함수보다 구조화하는 데 더 나을 것이다.  
이 패턴은 주로 일회성 작업에 적합하다. init()이 완료되고 나면 객체에 접근할 수 없다. 객체의 참조를 유지하고 싶다면 init()의 마지막에 `return this;`를 추가하면 된다.

## 초기화 시점의 분기
최적화하는 패턴이다. 어떤 조건이 프로그램의 생명주기 동안 변경되지 않는 게 확실한 경우, 조건을 단 한 번만 확인하는 것이 좋다. 똑같은 조건을 매번 확인할 필요는 없으니 말이다.
```
// 인터페이스
var utils = {
    addListener: null,
    removeListener: null
};

// 구현
// 조건을 스크립트가 로딩될 때 한 번만 확인한다.
if (typeof window.addEventListener === 'function') {
    utils.addListener = function() {// ...};
    urils.removeListener = function() {// ...};
} else if (typeof document.attachEvent == 'function') {
    utils.addListener = function() {// ...};
    urils.removeListener = function() {// ...};
} else {
    utils.addListener = function() {// ...};
    urils.removeListener = function() {// ...};
}
```

## 메모이제이션 Memoization - 함수 프로퍼티
함수는 객체이기 때문에 프로퍼티를 가질 수 있다. 함수에 프로퍼티를 추가하여 반환값을 캐시하면 다음 호출 시점에 복잡한 연산을 반복하지 않을 수 있다.
```
var myFunc = function (param) {
    var cacheKey = JSON.stringfy(Array.prototype.slice.call(arguments)), 
        result;

    if (!myFunc.cache[param]) {
        result = {};
        // ... 복잡한 연산
        myFunc.cache[param] = result;
    }
    return myFunc.cache[param];
};

// 캐시 저장공간
myFunc.cache = {};
```
직렬화하면 객체를 식별하게 될 수 없게 된다. 만약 같은 프로퍼티를 가지는 두 개의 다른 객체를 직렬화하면 두 객체는 같은 캐시 항목을 공유하게 된다.

## 설정 객체
좀 더 깨끗한 API를 제공하는 방법이다.  
요구사항이 변경되어 함수의 매개변수를 점점 늘려야 할 상황이 올 것이다. 이 때 많은 수의 매개변수를 전달하는 것보다 모든 매개변수를 하나의 객체로 만들어 전달하는 것이 더 낫다.  
```
var conf = {
    username: "nnn",
    first: "fff",
    last: "last"
};

addPerson(conf);
```
이렇게 객체를 매개변수로 전달하면 매개변수와 순서를 기억할 필요가 없고, 선택적인 매개변수를 안전하게 생략할 수 있고, 매개변수를 추가하거나 제거하기도 쉽다.

## 커리 Curry - 부분적인 함수
### 함수 적용
```
var hi = function (who) {
    return "Hi" + (who ? ", " + who : "") + "!";
};

hi(); //Hi!
hi('World'); //Hi, World!

// 함수를 적용한다
hi.apply(null, ['World']); //Hi, World!;
```
- `apply()`  
첫 번째 매개변수 : `this`와 바인딩할 객체. null이면 `this`는 전역 객체를 가리킨다.  
두 번째 매개변수 : 배열 또는 인자. 함수 내부에서 `arguments` 객체로 사용.
- `call()`  
`apply()`는 함수의 매개변수로 배열을 지정하고, `call()`은 매개변수를 단 하나만 지정한다.

### 부분적인 적용
위에서 봤듯이 함수의 호출이란 실제로는 인자의 묶음을 함수에 적용하는 것이다. 그렇다면 인자 전부가 아니라 일부 인자만 전달하려면?
```
// 이 코드는 예시로 유효하지 않음

function add(x, y) {
    return x + y;
}

add(5, 4);

function add(5, y) {
    return 5 + y;
}

function add(5, 4) {
    return 5 + 4;
}
```
이렇게 부분적으로 생각해볼 수 있다. 부분적인 적용을 실행한 결과는 또 다른 함수이며 이 함수는 다른 인자 값을 적용하여 호출할 수 있다.  
위의 예시처럼 함수가 부분적인 적용을 이해하고 처리할 수 있도록 만드는 과정을 커링이라고 한다.

### 커링 Curring
```
function add(x, y) {
    if (typeof oldy === "undefined") {
        return function (y) {
            return x + y;
        }
    }
    return x + y;
}

typeof add(5); //function
add(3)(4); //7

var add2000 = add(2000);
add2000(20) //2020
```
다음의 예제에서는 어떤 함수라도 부분적인 매개변수를 받는 새로운 함수로 변형하는 범용 함수를 보여준다.
```
function curry(fn) {
    var slice = Array.prototype.slice,
        stored_args = slice.call(arguments, 1);

    return function () {
        var new_args = slice.call(arguments),
            args = stored_args.concat(new_args);

        return fn.apply(null, args);
    };
}
```
`curry()`가 처음 호출될 때 지역 변수 `stored_args`에 첫 번째 인자를 떼어낸 인자를 저장하고 새로운 함수를 반환한다. 새로운 함수는 이미 일부 적용된 인자인 `stored_args`와 새로운 인자 `new_args`를 합친 다음 클로저에 저장되어 있는 원래의 함수 `fn`에 적용한다. 그리고 아래와 같이 사용한다.
```
function add(a, b, c, d, e) {
    return a + b + c + d + e;
}

var newAdd = curry(add, 1, 2, 3);
newAdd(4, 5); //15
```
어떤 함수를 호출할 때 대부분의 매개변수가 항상 비슷하다면 커링을 사용하기 좋다. 매개변수 일부를 적용하면 매번 인자를 전달하지 않아도 원본 함수가 기대하는 전체 목록을 미리 채워놓을 것이다.