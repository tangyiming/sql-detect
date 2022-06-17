module.exports = {
    preset: '@vue/cli-plugin-unit-jest',
    collectCoverage: true,
    collectCoverageFrom: ['**/*.{js,vue}', '!**/node_modules/**', '!**/dist/**'], //排除掉dist，否则生成报告报错
    coverageReporters: ['html', 'text-summary'],
}
