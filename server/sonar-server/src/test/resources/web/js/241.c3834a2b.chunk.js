webpackJsonp([241],{2140:function(e,t,n){"use strict";function a(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function r(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}function o(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}Object.defineProperty(t,"__esModule",{value:!0});var s=n(0),i=n.n(s),c=n(6),l=n.n(c),u=n(30),p=n.n(u),m=n(4),d=(n.n(m),n(10)),h=n(2163),f=n(2281),b=n(2435),k=n(2285),g=n(2436),y=n(2439),E=n(2164),v=n(2248),_=n(2716),C=(n.n(_),n(153)),N=n(2),S=function(){function e(e,t){var n=[],a=!0,r=!1,o=void 0;try{for(var s,i=e[Symbol.iterator]();!(a=(s=i.next()).done)&&(n.push(s.value),!t||n.length!==t);a=!0);}catch(e){r=!0,o=e}finally{try{!a&&i.return&&i.return()}finally{if(r)throw o}}return n}return function(t,n){if(Array.isArray(t))return t;if(Symbol.iterator in Object(t))return e(t,n);throw new TypeError("Invalid attempt to destructure non-iterable instance")}}(),w=function(){function e(e,t){for(var n=0;n<t.length;n++){var a=t[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}return function(t,n,a){return n&&e(t.prototype,n),a&&e(t,a),t}}(),O=i.a.createElement("div",{className:"page page-limited"},i.a.createElement("i",{className:"spinner"})),P=function(e){function t(){var e,n,o,s;a(this,t);for(var i=arguments.length,c=Array(i),l=0;l<i;l++)c[l]=arguments[l];return n=o=r(this,(e=t.__proto__||Object.getPrototypeOf(t)).call.apply(e,[this].concat(c))),o.state={loading:!0,tasks:[],query:"",pendingCount:0,failingCount:0},s=n,r(o,s)}return o(t,e),w(t,[{key:"componentWillMount",value:function(){this.loadTasksDebounced=n.i(m.debounce)(this.loadTasks.bind(this),h.b)}},{key:"componentDidMount",value:function(){var e=this;this.mounted=!0,n.i(E.b)().then(function(t){e.setState({types:t}),e.loadTasks()})}},{key:"componentDidUpdate",value:function(e){e.component===this.props.component&&e.location===this.props.location||this.loadTasksDebounced()}},{key:"componentWillUnmount",value:function(){this.mounted=!1}},{key:"loadTasks",value:function(){var e=this;this.setState({loading:!0});var t=this.props.location.query.status||h.c.status,a=this.props.location.query.taskType||h.c.taskType,r=this.props.location.query.currents||h.c.currents,o=this.props.location.query.minSubmittedAt||h.c.minSubmittedAt,s=this.props.location.query.maxExecutedAt||h.c.maxExecutedAt,i=this.props.location.query.query||h.c.query,c={status:t,taskType:a,currents:r,minSubmittedAt:o,maxExecutedAt:s,query:i},l=n.i(v.a)(c);this.props.component&&(l.componentId=this.props.component.id),Promise.all([n.i(E.c)(l),n.i(E.d)(l.componentId)]).then(function(t){if(e.mounted){var a=S(t,2),r=a[0],o=a[1],s=r.tasks,i=o.pending,c=o.failing,l=n.i(m.uniq)(s.map(function(e){return e.organization}).filter(function(e){return e}));e.props.fetchOrganizations(l),e.setState({tasks:s,pendingCount:i,failingCount:c,loading:!1})}})}},{key:"handleFilterUpdate",value:function(e){var t=Object.assign({},this.props.location.query,e);Object.keys(h.c).forEach(function(e){t[e]===h.c[e]&&delete t[e]}),this.context.router.push({pathname:this.props.location.pathname,query:t})}},{key:"handleCancelTask",value:function(e){var t=this;this.setState({loading:!0}),n.i(E.e)(e.id).then(function(e){if(t.mounted){var a=n.i(v.b)(t.state.tasks,e);t.setState({tasks:a,loading:!1})}})}},{key:"handleFilterTask",value:function(e){this.handleFilterUpdate({query:e.componentKey})}},{key:"handleShowFailing",value:function(){this.handleFilterUpdate(Object.assign({},h.c,{status:h.a.FAILED,currents:h.d.ONLY_CURRENTS}))}},{key:"handleCancelAllPending",value:function(){var e=this;this.setState({loading:!0}),n.i(E.f)().then(function(){e.mounted&&e.loadTasks()})}},{key:"render",value:function(){var e=this.props.component,t=this.state,a=t.loading,r=t.types,o=t.tasks,s=t.pendingCount,c=t.failingCount;if(!r)return O;var l=this.props.location.query.status||h.c.status,u=this.props.location.query.taskType||h.c.taskType,m=this.props.location.query.currents||h.c.currents,d=this.props.location.query.minSubmittedAt||"",E=this.props.location.query.maxExecutedAt||"",v=this.props.location.query.query||"";return i.a.createElement("div",{className:"page page-limited"},i.a.createElement(p.a,{title:n.i(N.translate)("background_tasks.page")}),i.a.createElement(f.a,{component:e}),i.a.createElement(k.a,{component:e,pendingCount:s,failingCount:c,onShowFailing:this.handleShowFailing.bind(this),onCancelAllPending:this.handleCancelAllPending.bind(this)}),i.a.createElement(g.a,{loading:a,component:e,status:l,currents:m,minSubmittedAt:d,maxExecutedAt:E,query:v,taskType:u,types:r,onFilterUpdate:this.handleFilterUpdate.bind(this),onReload:this.loadTasksDebounced}),i.a.createElement(y.a,{loading:a,component:e,tasks:o,onCancelTask:this.handleCancelTask.bind(this),onFilterTask:this.handleFilterTask.bind(this)}),i.a.createElement(b.a,{tasks:o}))}}]),t}(i.a.PureComponent);P.contextTypes={router:l.a.object.isRequired};var A={fetchOrganizations:C.b};t.default=n.i(d.connect)(null,A)(P)},2163:function(e,t,n){"use strict";n.d(t,"a",function(){return a}),n.d(t,"e",function(){return r}),n.d(t,"d",function(){return o}),n.d(t,"c",function(){return s}),n.d(t,"b",function(){return i});var a={ALL:"__ALL__",ALL_EXCEPT_PENDING:"__ALL_EXCEPT_PENDING__",PENDING:"PENDING",IN_PROGRESS:"IN_PROGRESS",SUCCESS:"SUCCESS",FAILED:"FAILED",CANCELED:"CANCELED"},r="ALL_TYPES",o={ALL:"__ALL__",ONLY_CURRENTS:"CURRENTS"},s={status:a.ALL_EXCEPT_PENDING,taskType:r,currents:o.ALL,minSubmittedAt:"",maxExecutedAt:"",query:""},i=250},2164:function(e,t,n){"use strict";function a(e){return n.i(m.getJSON)("/api/ce/activity",e)}function r(e){var t={};return e&&Object.assign(t,{componentId:e}),n.i(m.getJSON)("/api/ce/activity_status",t)}function o(e,t){return n.i(m.getJSON)("/api/ce/task",{id:e,additionalFields:t}).then(function(e){return e.task})}function s(e){return n.i(m.post)("/api/ce/cancel",{id:e}).then(function(){return o(e)},function(){return o(e)})}function i(){return n.i(m.post)("/api/ce/cancel_all")}function c(e){return n.i(m.getJSON)("/api/ce/component",{componentKey:e})}function l(){return n.i(m.getJSON)("/api/ce/task_types").then(function(e){return e.taskTypes})}function u(){return n.i(m.getJSON)("/api/ce/worker_count").catch(d.a)}function p(e){return n.i(m.post)("/api/ce/set_worker_count",{count:e}).catch(d.a)}t.c=a,t.d=r,t.g=o,t.e=s,t.f=i,t.a=c,t.b=l,t.h=u,t.i=p;var m=n(19),d=n(42)},2169:function(e,t,n){"use strict";function a(e){var t=e.link,n=void 0===t||t,a=e.organization;return e.shouldBeDisplayed&&a?r.createElement("span",null,n?r.createElement(i.a,{className:e.linkClassName,organization:a},a.name):a.name,r.createElement("span",{className:"slash-separator"})):null}var r=n(0),o=(n.n(r),n(10)),s=n(11),i=n(392),c=function(e,t){return{organization:n.i(s.getOrganizationByKey)(e,t.organizationKey),shouldBeDisplayed:n.i(s.areThereCustomOrganizations)(e)}};t.a=n.i(o.connect)(c)(a)},2217:function(e,t,n){"use strict";function a(e){var t=e.children,a=e.date,u=e.long;return r.createElement(o.g,i({children:t,value:n.i(s.parseDate)(a)},u?l:c))}t.a=a;var r=n(0),o=(n.n(r),n(62)),s=n(54),i=this&&this.__assign||Object.assign||function(e){for(var t,n=1,a=arguments.length;n<a;n++){t=arguments[n];for(var r in t)Object.prototype.hasOwnProperty.call(t,r)&&(e[r]=t[r])}return e},c={hour:"numeric",minute:"numeric"},l={hour:"numeric",minute:"numeric",second:"numeric"}},2248:function(e,t,n){"use strict";function a(e,t){return e.map(function(e){return e.id===t.id?t:e})}function r(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{},t={};return e.status===i.a.ALL?t.status=[i.a.PENDING,i.a.IN_PROGRESS,i.a.SUCCESS,i.a.FAILED,i.a.CANCELED].join():e.status===i.a.ALL_EXCEPT_PENDING?t.status=[i.a.IN_PROGRESS,i.a.SUCCESS,i.a.FAILED,i.a.CANCELED].join():t.status=e.status,e.taskType!==i.e&&(t.type=e.taskType),e.currents!==i.d.ALL&&(t.onlyCurrents=!0),e.minSubmittedAt&&(t.minSubmittedAt=e.minSubmittedAt),e.maxExecutedAt&&(t.maxExecutedAt=e.maxExecutedAt),e.query&&(t.componentQuery=e.query),1!==e.lastPage&&(t.p=e.lastPage),t}function o(e,t){return""+e+t}function s(e){if(!e)return"";if(e>=l){return o(Math.round(e/l),"min")}if(e>=c){return o(Math.round(e/c),"s")}return o(e,"ms")}t.b=a,t.a=r,t.c=s;var i=n(2163),c=1e3,l=60*c},2280:function(e,t,n){"use strict";var a=n(0),r=(n.n(a),n(203)),o=n(2163),s=n(2),i=this&&this.__extends||function(){var e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])};return function(t,n){function a(){this.constructor=t}e(t,n),t.prototype=null===n?Object.create(n):(a.prototype=n.prototype,new a)}}(),c=function(e){function t(){var t=null!==e&&e.apply(this,arguments)||this;return t.handleChange=function(e){var n=e?o.d.ONLY_CURRENTS:o.d.ALL;t.props.onChange(n)},t}return i(t,e),t.prototype.render=function(){var e=this.props.value===o.d.ONLY_CURRENTS;return a.createElement("div",{className:"bt-search-form-field"},a.createElement(r.a,{checked:e,onCheck:this.handleChange},a.createElement("span",{className:"little-spacer-left"},n.i(s.translate)("yes"))))},t}(a.PureComponent);t.a=c},2281:function(e,t,n){"use strict";function a(e){return r.createElement("header",{className:"page-header"},r.createElement("h1",{className:"page-title"},n.i(s.translate)("background_tasks.page")),!e.component&&r.createElement("div",{className:"page-actions"},r.createElement(o.a,null)),r.createElement("p",{className:"page-description"},n.i(s.translate)("background_tasks.page.description")))}t.a=a;var r=n(0),o=(n.n(r),n(2295)),s=n(2)},2282:function(e,t,n){"use strict";function a(e){return r.createElement(o.a,{position:e.popupPosition,customClass:"bubble-popup-bottom-right"},r.createElement("div",{className:"abs-width-400"},r.createElement("h6",{className:"spacer-bottom"},n.i(s.translate)("background_tasks.add_more_workers")),r.createElement("p",{className:"big-spacer-bottom markdown"},n.i(s.translate)("background_tasks.add_more_workers.text")),r.createElement("p",null,r.createElement("a",{href:"https://redirect.sonarsource.com/editions/enterprise.html",target:"_blank"},n.i(s.translate)("learn_more")))))}t.a=a;var r=n(0),o=(n.n(r),n(68)),s=n(2)},2283:function(e,t,n){"use strict";var a=n(0),r=(n.n(a),n(37)),o=n.n(r),s=n(2164),i=n(2),c=this&&this.__extends||function(){var e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])};return function(t,n){function a(){this.constructor=t}e(t,n),t.prototype=null===n?Object.create(n):(a.prototype=n.prototype,new a)}}(),l=function(e){function t(){var t=null!==e&&e.apply(this,arguments)||this;return t.state={},t.handleCloseClick=function(e){e.preventDefault(),t.props.onClose()},t}return c(t,e),t.prototype.componentDidMount=function(){this.mounted=!0,this.loadScannerContext()},t.prototype.componentWillUnmount=function(){this.mounted=!1},t.prototype.loadScannerContext=function(){var e=this;n.i(s.g)(this.props.task.id,["scannerContext"]).then(function(t){e.mounted&&e.setState({scannerContext:t.scannerContext})})},t.prototype.render=function(){var e=this.props.task,t=this.state.scannerContext;return a.createElement(o.a,{isOpen:!0,contentLabel:"scanner context",className:"modal modal-large",overlayClassName:"modal-overlay",onRequestClose:this.props.onClose},a.createElement("div",{className:"modal-head"},a.createElement("h2",null,n.i(i.translate)("background_tasks.scanner_context"),": ",e.componentName," [",n.i(i.translate)("background_task.type",e.type),"]")),a.createElement("div",{className:"modal-body modal-container"},null!=t?a.createElement("pre",{className:"js-task-scanner-context"},t):a.createElement("i",{className:"spinner"})),a.createElement("div",{className:"modal-foot"},a.createElement("a",{href:"#",className:"js-modal-close",onClick:this.handleCloseClick},n.i(i.translate)("close"))))},t}(a.PureComponent);t.a=l},2284:function(e,t,n){"use strict";var a=n(0),r=(n.n(a),n(37)),o=n.n(r),s=n(2164),i=n(2),c=this&&this.__extends||function(){var e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])};return function(t,n){function a(){this.constructor=t}e(t,n),t.prototype=null===n?Object.create(n):(a.prototype=n.prototype,new a)}}(),l=function(e){function t(){var t=null!==e&&e.apply(this,arguments)||this;return t.state={loading:!0},t.handleCloseClick=function(e){e.preventDefault(),t.props.onClose()},t}return c(t,e),t.prototype.componentDidMount=function(){this.mounted=!0,this.loadStacktrace()},t.prototype.componentWillUnmount=function(){this.mounted=!1},t.prototype.loadStacktrace=function(){var e=this;n.i(s.g)(this.props.task.id,["stacktrace"]).then(function(t){e.mounted&&e.setState({loading:!1,stacktrace:t.errorStacktrace})})},t.prototype.render=function(){var e=this.props.task,t=this.state,r=t.loading,s=t.stacktrace;return a.createElement(o.a,{isOpen:!0,contentLabel:"stacktrace",className:"modal modal-large",overlayClassName:"modal-overlay",onRequestClose:this.props.onClose},a.createElement("div",{className:"modal-head"},a.createElement("h2",null,n.i(i.translate)("background_tasks.error_stacktrace"),": ",e.componentName," [",n.i(i.translate)("background_task.type",e.type),"]")),a.createElement("div",{className:"modal-body modal-container"},r?a.createElement("i",{className:"spinner"}):s?a.createElement("div",null,a.createElement("h4",{className:"spacer-bottom"},n.i(i.translate)("background_tasks.error_stacktrace")),a.createElement("pre",{className:"js-task-stacktrace"},s)):a.createElement("div",null,a.createElement("h4",{className:"spacer-bottom"},n.i(i.translate)("background_tasks.error_message")),a.createElement("pre",{className:"js-task-error-message"},e.errorMessage))),a.createElement("div",{className:"modal-foot"},a.createElement("a",{href:"#",className:"js-modal-close",onClick:this.handleCloseClick},n.i(i.translate)("close"))))},t}(a.PureComponent);t.a=l},2285:function(e,t,n){"use strict";var a=n(10),r=n(2437),o=n(11),s=function(e){return{isSystemAdmin:!!n.i(o.getAppState)(e).canAdmin}};t.a=n.i(a.connect)(s)(r.a)},2286:function(e,t,n){"use strict";function a(e){var t=e.task,n=e.component,a=e.onCancelTask,m=e.onFilterTask,d=e.previousTask;return r.createElement("tr",null,r.createElement(o.a,{status:t.status}),r.createElement(s.a,{task:t}),r.createElement(i.a,{id:t.id}),r.createElement(c.a,{submittedAt:t.submittedAt,prevSubmittedAt:d&&d.submittedAt}),r.createElement(l.a,{date:t.submittedAt}),r.createElement(l.a,{date:t.startedAt,baseDate:t.submittedAt}),r.createElement(l.a,{date:t.executedAt,baseDate:t.submittedAt}),r.createElement(u.a,{ms:t.executionTimeMs}),r.createElement(p.a,{component:n,task:t,onFilterTask:m,onCancelTask:a}))}t.a=a;var r=n(0),o=(n.n(r),n(2293)),s=n(2288),i=n(2292),c=n(2290),l=n(2289),u=n(2291),p=n(2287)},2287:function(e,t,n){"use strict";var a=n(0),r=(n.n(a),n(2283)),o=n(2284),s=n(2163),i=n(2),c=this&&this.__extends||function(){var e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])};return function(t,n){function a(){this.constructor=t}e(t,n),t.prototype=null===n?Object.create(n):(a.prototype=n.prototype,new a)}}(),l=function(e){function t(){var t=null!==e&&e.apply(this,arguments)||this;return t.state={scannerContextOpen:!1,stacktraceOpen:!1},t.handleFilterClick=function(e){e.preventDefault(),t.props.onFilterTask(t.props.task)},t.handleCancelClick=function(e){e.preventDefault(),t.props.onCancelTask(t.props.task)},t.handleShowScannerContextClick=function(e){e.preventDefault(),t.setState({scannerContextOpen:!0})},t.closeScannerContext=function(){return t.setState({scannerContextOpen:!1})},t.handleShowStacktraceClick=function(e){e.preventDefault(),t.setState({stacktraceOpen:!0})},t.closeStacktrace=function(){return t.setState({stacktraceOpen:!1})},t}return c(t,e),t.prototype.render=function(){var e=this.props,t=e.component,c=e.task,l=void 0==t,u=c.status===s.a.PENDING,p=void 0!=c.errorMessage;return l||u||c.hasScannerContext||p?a.createElement("td",{className:"thin nowrap"},a.createElement("div",{className:"dropdown js-task-action"},a.createElement("button",{className:"dropdown-toggle","data-toggle":"dropdown"},a.createElement("i",{className:"icon-dropdown"})),a.createElement("ul",{className:"dropdown-menu dropdown-menu-right"},l&&c.componentName&&a.createElement("li",null,a.createElement("a",{className:"js-task-filter",href:"#",onClick:this.handleFilterClick},a.createElement("i",{className:"spacer-right icon-filter icon-gray"}),n.i(i.translateWithParameters)("background_tasks.filter_by_component_x",c.componentName))),u&&a.createElement("li",null,a.createElement("a",{className:"js-task-cancel",href:"#",onClick:this.handleCancelClick},a.createElement("i",{className:"spacer-right icon-delete"}),n.i(i.translate)("background_tasks.cancel_task"))),c.hasScannerContext&&a.createElement("li",null,a.createElement("a",{className:"js-task-show-scanner-context",href:"#",onClick:this.handleShowScannerContextClick},a.createElement("i",{className:"spacer-right icon-list icon-gray"}),n.i(i.translate)("background_tasks.show_scanner_context"))),p&&a.createElement("li",null,a.createElement("a",{className:"js-task-show-stacktrace",href:"#",onClick:this.handleShowStacktraceClick},a.createElement("i",{className:"spacer-right icon-list icon-red"}),n.i(i.translate)("background_tasks.show_stacktrace"))))),this.state.scannerContextOpen&&a.createElement(r.a,{onClose:this.closeScannerContext,task:c}),this.state.stacktraceOpen&&a.createElement(o.a,{onClose:this.closeStacktrace,task:c})):a.createElement("td",null," ")},t}(a.PureComponent);t.a=l},2288:function(e,t,n){"use strict";function a(e){var t=e.task;return t.componentKey?r.createElement("td",null,"SHORT"===t.branchType&&r.createElement(u.a,{className:"little-spacer-right"}),"LONG"===t.branchType&&r.createElement(p.a,{className:"little-spacer-right"}),!t.branchType&&t.componentQualifier&&r.createElement("span",{className:"little-spacer-right"},r.createElement(i.a,{qualifier:t.componentQualifier})),t.organization&&r.createElement(c.a,{organizationKey:t.organization}),t.componentName&&r.createElement(o.Link,{className:"spacer-right",to:n.i(l.b)(t.componentKey,t.branch)},t.componentName,t.branch&&r.createElement("span",{className:"text-limited text-text-top",title:t.branch},r.createElement("span",{style:{marginLeft:5,marginRight:5}},"/"),t.branch)),r.createElement(s.a,{type:t.type})):r.createElement("td",null,r.createElement("span",{className:"note"},t.id),r.createElement(s.a,{type:t.type}))}t.a=a;var r=n(0),o=(n.n(r),n(9)),s=n(2294),i=n(92),c=n(2169),l=n(34),u=n(412),p=n(411)},2289:function(e,t,n){"use strict";function a(e){var t=e.date,a=e.baseDate,i=t&&n.i(s.parseDate)(t),c=a&&n.i(s.parseDate)(a),l=i&&c&&n.i(s.isValidDate)(i)&&n.i(s.isValidDate)(c)?n.i(s.differenceInDays)(i,c):0;return r.createElement("td",{className:"thin nowrap text-right"},l>0&&r.createElement("span",{className:"text-warning little-spacer-right"},"(+"+l+"d)"),i&&n.i(s.isValidDate)(i)?r.createElement(o.a,{date:i,long:!0}):"")}t.a=a;var r=n(0),o=(n.n(r),n(2217)),s=n(54)},2290:function(e,t,n){"use strict";function a(e){var t=e.submittedAt,a=e.prevSubmittedAt,i=!a||!n.i(s.isSameDay)(n.i(s.parseDate)(t),n.i(s.parseDate)(a));return r.createElement("td",{className:"thin nowrap text-right"},i?r.createElement(o.a,{date:t,long:!0}):"")}t.a=a;var r=n(0),o=(n.n(r),n(388)),s=n(54)},2291:function(e,t,n){"use strict";function a(e){var t=e.ms;return r.createElement("td",{className:"thin nowrap text-right"},t&&n.i(o.c)(t))}t.a=a;var r=n(0),o=(n.n(r),n(2248))},2292:function(e,t,n){"use strict";function a(e){var t=e.id;return r.createElement("td",{className:"thin nowrap"},r.createElement("div",{className:"note"},t))}t.a=a;var r=n(0);n.n(r)},2293:function(e,t,n){"use strict";function a(e){var t,a=e.status;switch(a){case o.a.PENDING:t=r.createElement(s.a,null);break;case o.a.IN_PROGRESS:t=r.createElement("i",{className:"spinner"});break;case o.a.SUCCESS:t=r.createElement("span",{className:"badge badge-success"},n.i(i.translate)("background_task.status.SUCCESS"));break;case o.a.FAILED:t=r.createElement("span",{className:"badge badge-danger"},n.i(i.translate)("background_task.status.FAILED"));break;case o.a.CANCELED:t=r.createElement("span",{className:"badge badge-muted"},n.i(i.translate)("background_task.status.CANCELED"));break;default:t=""}return r.createElement("td",{className:"thin spacer-right"},t)}t.a=a;var r=n(0),o=(n.n(r),n(2163)),s=n(716),i=n(2)},2294:function(e,t,n){"use strict";function a(e){var t=e.type;return r.createElement("span",{className:"display-inline-block note"},"[",n.i(o.translate)("background_task.type",t),"]")}t.a=a;var r=n(0),o=(n.n(r),n(2))},2295:function(e,t,n){"use strict";var a=n(0),r=(n.n(a),n(2296)),o=n(2282),s=n(24),i=n(2164),c=n(2),l=n(391),u=n(70),p=this&&this.__extends||function(){var e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])};return function(t,n){function a(){this.constructor=t}e(t,n),t.prototype=null===n?Object.create(n):(a.prototype=n.prototype,new a)}}(),m=function(e){function t(){var t=null!==e&&e.apply(this,arguments)||this;return t.state={canSetWorkerCount:!1,formOpen:!1,loading:!0,noSupportPopup:!1,workerCount:1},t.loadWorkers=function(){t.setState({loading:!0}),n.i(i.h)().then(function(e){var n=e.canSetWorkerCount,a=e.value;t.mounted&&t.setState({canSetWorkerCount:n,loading:!1,workerCount:a})})},t.closeForm=function(e){return e?t.setState({formOpen:!1,workerCount:e}):t.setState({formOpen:!1})},t.handleChangeClick=function(e){e.preventDefault(),t.setState({formOpen:!0})},t.handleHelpClick=function(e){e.preventDefault(),e.stopPropagation(),t.toggleNoSupportPopup()},t.toggleNoSupportPopup=function(e){void 0!=e?t.setState({noSupportPopup:e}):t.setState(function(e){return{noSupportPopup:!e.noSupportPopup}})},t}return p(t,e),t.prototype.componentDidMount=function(){this.mounted=!0,this.loadWorkers()},t.prototype.componentWillUnmount=function(){this.mounted=!1},t.prototype.render=function(){var e=this.state,t=e.canSetWorkerCount,i=e.formOpen,p=e.loading,m=e.workerCount;return a.createElement("div",null,!p&&m>1&&a.createElement(s.a,{overlay:n.i(c.translate)("background_tasks.number_of_workers.warning")},a.createElement("i",{className:"icon-alert-warn little-spacer-right bt-workers-warning-icon"})),n.i(c.translate)("background_tasks.number_of_workers"),p?a.createElement("i",{className:"spinner little-spacer-left"}):a.createElement("strong",{className:"little-spacer-left"},m),!p&&t&&a.createElement(s.a,{overlay:n.i(c.translate)("background_tasks.change_number_of_workers")},a.createElement("a",{className:"icon-edit spacer-left",href:"#",onClick:this.handleChangeClick})),!p&&!t&&a.createElement("span",{className:"spacer-left"},a.createElement("a",{className:"link-no-underline",href:"#",onClick:this.handleHelpClick},a.createElement(l.a,{className:"text-text-bottom",fill:"#cdcdcd"})),a.createElement(u.a,{isOpen:this.state.noSupportPopup,position:"bottomright",popup:a.createElement(o.a,null),togglePopup:this.toggleNoSupportPopup})),i&&a.createElement(r.a,{onClose:this.closeForm,workerCount:this.state.workerCount}))},t}(a.PureComponent);t.a=m},2296:function(e,t,n){"use strict";var a=n(0),r=(n.n(a),n(37)),o=n.n(r),s=n(61),i=(n.n(s),n(4)),c=(n.n(i),n(2164)),l=n(2),u=this&&this.__extends||function(){var e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])};return function(t,n){function a(){this.constructor=t}e(t,n),t.prototype=null===n?Object.create(n):(a.prototype=n.prototype,new a)}}(),p=function(e){function t(t){var a=e.call(this,t)||this;return a.handleClose=function(){return a.props.onClose()},a.handleWorkerCountChange=function(e){return a.setState({newWorkerCount:e.value})},a.handleSubmit=function(e){e.preventDefault(),a.setState({submitting:!0});var t=a.state.newWorkerCount;n.i(c.i)(t).then(function(){a.mounted&&a.props.onClose(t)},function(){a.mounted&&a.setState({submitting:!1})})},a.state={newWorkerCount:t.workerCount,submitting:!1},a}return u(t,e),t.prototype.componentDidMount=function(){this.mounted=!0},t.prototype.componentWillUnmount=function(){this.mounted=!1},t.prototype.render=function(){var e=n.i(i.times)(10).map(function(e,t){return{label:String(t+1),value:t+1}});return a.createElement(o.a,{isOpen:!0,contentLabel:n.i(l.translate)("background_tasks.change_number_of_workers"),className:"modal",overlayClassName:"modal-overlay",onRequestClose:this.handleClose},a.createElement("header",{className:"modal-head"},a.createElement("h2",null,n.i(l.translate)("background_tasks.change_number_of_workers"))),a.createElement("form",{onSubmit:this.handleSubmit},a.createElement("div",{className:"modal-body"},a.createElement(s,{className:"input-tiny spacer-top",clearable:!1,onChange:this.handleWorkerCountChange,options:e,searchable:!1,value:this.state.newWorkerCount}),a.createElement("div",{className:"big-spacer-top alert alert-success markdown"},n.i(l.translate)("background_tasks.change_number_of_workers.hint"))),a.createElement("footer",{className:"modal-foot"},a.createElement("div",null,this.state.submitting&&a.createElement("i",{className:"spinner spacer-right"}),a.createElement("button",{disabled:this.state.submitting,type:"submit"},n.i(l.translate)("save")),a.createElement("button",{type:"reset",className:"button-link",onClick:this.handleClose},n.i(l.translate)("cancel"))))))},t}(a.PureComponent);t.a=p},2434:function(e,t,n){"use strict";function a(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function r(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}function o(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}var s=n(15),i=n.n(s),c=n(0),l=n.n(c),u=n(54),p=n(2),m=function(){function e(e,t){for(var n=0;n<t.length;n++){var a=t[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}return function(t,n,a){return n&&e(t.prototype,n),a&&e(t,a),t}}(),d=function(e){function t(){return a(this,t),r(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments))}return o(t,e),m(t,[{key:"componentDidMount",value:function(){this.attachDatePicker()}},{key:"componentDidUpdate",value:function(){this.attachDatePicker()}},{key:"attachDatePicker",value:function(){var e={dateFormat:"yy-mm-dd",changeMonth:!0,changeYear:!0,onSelect:this.handleChange.bind(this)};i.a.fn&&i.a.fn.datepicker&&(i()(this.refs.minDate).datepicker(e),i()(this.refs.maxDate).datepicker(e))}},{key:"handleChange",value:function(){var e={},t=n.i(u.parseDate)(this.refs.minDate.value),a=n.i(u.parseDate)(this.refs.maxDate.value);n.i(u.isValidDate)(t)&&(e.minSubmittedAt=n.i(u.toShortNotSoISOString)(t)),n.i(u.isValidDate)(a)&&(e.maxExecutedAt=n.i(u.toShortNotSoISOString)(a)),this.props.onChange(e)}},{key:"render",value:function(){var e=this.props,t=e.minSubmittedAt,a=e.maxExecutedAt;return l.a.createElement("div",{className:"nowrap"},l.a.createElement("input",{className:"input-small",value:t,onChange:function(){return!0},ref:"minDate",type:"text",placeholder:n.i(p.translate)("from")})," ",l.a.createElement("input",{className:"input-small",value:a,onChange:function(){return!0},ref:"maxDate",type:"text",placeholder:n.i(p.translate)("to")}))}}]),t}(c.Component);t.a=d},2435:function(e,t,n){"use strict";var a=n(0),r=n.n(a),o=n(2),s=function(e){return e.tasks.length<1e3?null:r.a.createElement("footer",{className:"spacer-top note text-center"},n.i(o.translateWithParameters)("max_results_reached",1e3))};t.a=s},2436:function(e,t,n){"use strict";function a(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function r(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}function o(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}var s=n(0),i=n.n(s),c=n(6),l=n.n(c),u=n(2438),p=n(2440),m=n(2280),d=n(2434),h=n(2163),f=n(2),b=function(){function e(e,t){for(var n=0;n<t.length;n++){var a=t[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}return function(t,n,a){return n&&e(t.prototype,n),a&&e(t,a),t}}(),k=function(e){function t(){return a(this,t),r(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments))}return o(t,e),b(t,[{key:"handleStatusChange",value:function(e){this.props.onFilterUpdate({status:e})}},{key:"handleTypeChange",value:function(e){this.props.onFilterUpdate({taskType:e})}},{key:"handleCurrentsChange",value:function(e){this.props.onFilterUpdate({currents:e})}},{key:"handleDateChange",value:function(e){this.props.onFilterUpdate(e)}},{key:"handleQueryChange",value:function(e){this.props.onFilterUpdate({query:e})}},{key:"handleReload",value:function(e){e.target.blur(),this.props.onReload()}},{key:"handleReset",value:function(e){e.preventDefault(),e.target.blur(),this.props.onFilterUpdate(h.c)}},{key:"renderSearchBox",value:function(){var e=this,t=this.props,a=t.component,r=t.query;return a?null:i.a.createElement("li",null,i.a.createElement("h6",{className:"bt-search-form-label"},n.i(f.translate)("background_tasks.search_by_task_or_component")),i.a.createElement("input",{onChange:function(t){return e.handleQueryChange(t.target.value)},value:r,ref:"searchInput",className:"js-search input-medium",type:"search",placeholder:n.i(f.translate)("search_verb")}))}},{key:"render",value:function(){var e=this.props,t=e.loading,a=e.component,r=e.types,o=e.status,s=e.taskType,c=e.currents,l=e.minSubmittedAt,h=e.maxExecutedAt;return i.a.createElement("section",{className:"big-spacer-top big-spacer-bottom"},i.a.createElement("ul",{className:"bt-search-form"},i.a.createElement("li",null,i.a.createElement("h6",{className:"bt-search-form-label"},n.i(f.translate)("status")),i.a.createElement(u.a,{value:o,onChange:this.handleStatusChange.bind(this)})),r.length>1&&i.a.createElement("li",null,i.a.createElement("h6",{className:"bt-search-form-label"},n.i(f.translate)("type")),i.a.createElement(p.a,{value:s,types:r,onChange:this.handleTypeChange.bind(this)})),!a&&i.a.createElement("li",null,i.a.createElement("h6",{className:"bt-search-form-label"},n.i(f.translate)("background_tasks.currents_filter.ONLY_CURRENTS")),i.a.createElement(m.a,{value:c,onChange:this.handleCurrentsChange.bind(this)})),i.a.createElement("li",null,i.a.createElement("h6",{className:"bt-search-form-label"},n.i(f.translate)("date")),i.a.createElement(d.a,{minSubmittedAt:l,maxExecutedAt:h,onChange:this.handleDateChange.bind(this)})),this.renderSearchBox(),i.a.createElement("li",{className:"bt-search-form-right nowrap"},i.a.createElement("button",{className:"js-reload",onClick:this.handleReload.bind(this),disabled:t},n.i(f.translate)("reload"))," ",i.a.createElement("button",{ref:"resetButton",onClick:this.handleReset.bind(this),disabled:t},n.i(f.translate)("reset_verb")))))}}]),t}(i.a.PureComponent);k.propTypes={loading:l.a.bool.isRequired,status:l.a.any.isRequired,taskType:l.a.any.isRequired,currents:l.a.any.isRequired,query:l.a.string.isRequired,onFilterUpdate:l.a.func.isRequired,onReload:l.a.func.isRequired},t.a=k},2437:function(e,t,n){"use strict";function a(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function r(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}function o(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}var s=n(0),i=n.n(s),c=n(24),l=n(2),u=function(){function e(e,t){for(var n=0;n<t.length;n++){var a=t[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}return function(t,n,a){return n&&e(t.prototype,n),a&&e(t,a),t}}(),p=function(e){function t(){var e,n,o,s;a(this,t);for(var i=arguments.length,c=Array(i),l=0;l<i;l++)c[l]=arguments[l];return n=o=r(this,(e=t.__proto__||Object.getPrototypeOf(t)).call.apply(e,[this].concat(c))),o.handleCancelAllPending=function(e){e.preventDefault(),e.currentTarget.blur(),o.props.onCancelAllPending()},o.handleShowFailing=function(e){e.preventDefault(),e.currentTarget.blur(),o.props.onShowFailing()},s=n,r(o,s)}return o(t,e),u(t,[{key:"renderPending",value:function(){return null==this.props.pendingCount?null:this.props.pendingCount>0?i.a.createElement("span",null,i.a.createElement("span",{className:"js-pending-count emphasised-measure"},this.props.pendingCount)," ",n.i(l.translate)("background_tasks.pending"),this.props.isSystemAdmin&&i.a.createElement(c.a,{overlay:n.i(l.translate)("background_tasks.cancel_all_tasks")},i.a.createElement("a",{className:"js-cancel-pending icon-delete spacer-left",href:"#",onClick:this.handleCancelAllPending}))):i.a.createElement("span",null,i.a.createElement("span",{className:"js-pending-count emphasised-measure"},this.props.pendingCount)," ",n.i(l.translate)("background_tasks.pending"))}},{key:"renderFailures",value:function(){return null==this.props.failingCount?null:this.props.component?null:this.props.failingCount>0?i.a.createElement("span",null,i.a.createElement(c.a,{overlay:n.i(l.translate)("background_tasks.failing_count")},i.a.createElement("a",{className:"js-failures-count emphasised-measure",href:"#",onClick:this.handleShowFailing},this.props.failingCount))," ",n.i(l.translate)("background_tasks.failures")):i.a.createElement("span",null,i.a.createElement(c.a,{overlay:n.i(l.translate)("background_tasks.failing_count")},i.a.createElement("span",{className:"js-failures-count emphasised-measure"},this.props.failingCount))," ",n.i(l.translate)("background_tasks.failures"))}},{key:"render",value:function(){return i.a.createElement("section",{className:"big-spacer-top big-spacer-bottom"},i.a.createElement("span",null,this.renderPending()),i.a.createElement("span",{className:"huge-spacer-left"},this.renderFailures()))}}]),t}(i.a.PureComponent);t.a=p},2438:function(e,t,n){"use strict";var a=n(0),r=n.n(a),o=n(61),s=n.n(o),i=n(2163),c=n(2),l=function(e){var t=e.value,a=e.onChange,o=[{value:i.a.ALL,label:n.i(c.translate)("background_task.status.ALL")},{value:i.a.ALL_EXCEPT_PENDING,label:n.i(c.translate)("background_task.status.ALL_EXCEPT_PENDING")},{value:i.a.PENDING,label:n.i(c.translate)("background_task.status.PENDING")},{value:i.a.IN_PROGRESS,label:n.i(c.translate)("background_task.status.IN_PROGRESS")},{value:i.a.SUCCESS,label:n.i(c.translate)("background_task.status.SUCCESS")},{value:i.a.FAILED,label:n.i(c.translate)("background_task.status.FAILED")},{value:i.a.CANCELED,label:n.i(c.translate)("background_task.status.CANCELED")}];return r.a.createElement(s.a,{value:t,onChange:function(e){return a(e.value)},className:"input-medium",options:o,clearable:!1,searchable:!1})};t.a=l},2439:function(e,t,n){"use strict";function a(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function r(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}function o(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}var s=n(0),i=n.n(s),c=n(8),l=n.n(c),u=n(2286),p=n(2),m=function(){function e(e,t){for(var n=0;n<t.length;n++){var a=t[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}return function(t,n,a){return n&&e(t.prototype,n),a&&e(t,a),t}}(),d=i.a.createElement("th",null," "),h=i.a.createElement("th",null," "),f=function(e){function t(){return a(this,t),r(this,(t.__proto__||Object.getPrototypeOf(t)).apply(this,arguments))}return o(t,e),m(t,[{key:"render",value:function(){var e=this.props,t=e.tasks,a=e.component,r=e.loading,o=e.onCancelTask,s=e.onFilterTask,c=l()("data zebra zebra-hover background-tasks",{"new-loading":r});return i.a.createElement("table",{className:c},i.a.createElement("thead",null,i.a.createElement("tr",null,i.a.createElement("th",null,n.i(p.translate)("background_tasks.table.status")),i.a.createElement("th",null,n.i(p.translate)("background_tasks.table.task")),i.a.createElement("th",null,n.i(p.translate)("background_tasks.table.id")),d,i.a.createElement("th",{className:"text-right"},n.i(p.translate)("background_tasks.table.submitted")),i.a.createElement("th",{className:"text-right"},n.i(p.translate)("background_tasks.table.started")),i.a.createElement("th",{className:"text-right"},n.i(p.translate)("background_tasks.table.finished")),i.a.createElement("th",{className:"text-right"},n.i(p.translate)("background_tasks.table.duration")),h)),i.a.createElement("tbody",null,t.map(function(e,t,n){return i.a.createElement(u.a,{key:e.id,task:e,tasks:n,component:a,onCancelTask:o,onFilterTask:s,previousTask:t>0?n[t-1]:void 0})})))}}]),t}(i.a.PureComponent);t.a=f},2440:function(e,t,n){"use strict";function a(e){if(Array.isArray(e)){for(var t=0,n=Array(e.length);t<e.length;t++)n[t]=e[t];return n}return Array.from(e)}var r=n(0),o=n.n(r),s=n(61),i=n.n(s),c=n(2163),l=n(2),u=function(e){var t=e.value,r=e.onChange,s=e.types,u=s.map(function(e){return{value:e,label:n.i(l.translate)("background_task.type",e)}}),p=[{value:c.e,label:n.i(l.translate)("background_task.type.ALL")}].concat(a(u));return o.a.createElement(i.a,{value:t,onChange:function(e){return r(e.value)},className:"input-medium",options:p,clearable:!1,searchable:!1})};t.a=u},2666:function(e,t,n){t=e.exports=n(27)(void 0),t.push([e.i,".bt-search-form{display:flex;align-items:flex-end}.bt-search-form>li+li{margin-left:40px}.bt-search-form-label{margin-bottom:4px}.bt-search-form-field{padding:4px 0}.bt-search-form-right{margin-left:auto!important}.bt-workers-warning-icon{position:relative;top:-1px}.bt-workers-warning-icon:before{color:#d3d3d3}",""])},2716:function(e,t,n){var a=n(2666);"string"==typeof a&&(a=[[e.i,a,""]]);var r={};r.transform=void 0;n(28)(a,r);a.locals&&(e.exports=a.locals)}});
//# sourceMappingURL=241.c3834a2b.chunk.js.map