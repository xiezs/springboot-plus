module.exports = {
  root: true,

  env: {
    browser: true,
    node: true,
    es6: true,
  },

  parserOptions: {
    parser: 'babel-eslint',
  },

  overrides: [
    {
      files: ['**/__tests__/*.{j,t}s?(x)'],
      env: {
        jest: true,
      },
    },
  ],

  extends: [
    'plugin:vue/recommended',
    'plugin:prettier/recommended',
    '@vue/prettier',
  ],

  plugins: ['vue'],

  // 各种eslint检查的规则
  rules: {
    'prettier/prettier': [
      0,
      {
        eslintIntegration: true,
        printWidth: 80,
        tabWidth: 2,
        useTabs: false,
        singleQuote: true,
        semi: true,
        trailingComma: 'all',
        bracketSpacing: true,
        jsxBracketSameLine: false,
        arrowParens: 'avoid',
      },
    ],
    'no-console': 'off',
    'no-debugger': 'off',
    'no-unused-vars': 'off',
    'vue/no-unused-vars': 'off',
    'no-useless-escape': 'off',
    quotes: [
      2,
      'single',
      {
        avoidEscape: true,
        allowTemplateLiterals: true,
      },
    ],
    'jsx-quotes': [2, 'prefer-single'],
    // 缩进为2个空格
    'vue/html-indent': [
      'error',
      2,
      {
        attribute: 1,
        alignAttributesVertically: true,
        ignores: [],
      },
    ],
    'vue/max-attributes-per-line': [
      2,
      {
        singleline: 10,
        multiline: {
          max: 1,
          allowFirstLine: false,
        },
      },
    ],
    'vue/html-self-closing': 'off',
    'vue/name-property-casing': ['error', 'PascalCase'],
    // allow async-await
    'generator-star-spacing': 'off',
    // allow debugger during development
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    // 关闭检测函数名称和调用它的左括号之间的空格
    // 'func-call-spacing': 'off',
    // 缩进为2个空格
    indent: ['error', 2],
    // 关闭检测未使用的变量
    'no-unused-vars': 'off',
    // 对象展开时总是要添加逗号，一行时行末不需要逗号
    // 'comma-dangle': ['error', 'always-multiline'],
    // 关闭禁用无效标签
    'no-tabs': 'off',
    // 关闭空行检测
    'no-multiple-empty-lines': 'off',
    // 关闭模板字符串检测
    'no-template-curly-in-string': 'off',
    'no-console': 'off',
  },
}
